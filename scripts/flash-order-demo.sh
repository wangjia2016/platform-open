#!/usr/bin/env bash
# Minimal flash-sale order demo (no frontend).
# Prerequisites: mall up on BASE_URL, Redis/MySQL/MQ ready, flash seed imported,
#                at least goods_id=1 exists, JWT_SECRET configured.
set -euo pipefail

BASE_URL="${BASE_URL:-http://127.0.0.1:8080/mall}"
APP_ID="${APP_ID:-wx-demo-app}"
NUMBER="${NUMBER:-1}"
SESSIONS_ID="${SESSIONS_ID:-}"
GOODS_ID="${GOODS_ID:-}"

need_cmd() {
  command -v "$1" >/dev/null 2>&1 || { echo "need command: $1" >&2; exit 1; }
}
need_cmd curl
need_cmd python3

json_get() {
  # usage: json_get '<json>' 'python expr on obj -> print'
  local payload="$1"
  local expr="$2"
  PAYLOAD="$payload" python3 -c "
import json, os, sys
raw = os.environ.get('PAYLOAD') or ''
try:
    obj = json.loads(raw)
except Exception as e:
    sys.stderr.write('invalid json: %s\n' % e)
    sys.exit(2)
val = ($expr)
if val is None:
    sys.exit(3)
print(val)
"
}

echo "==> 1) list flash sessions"
SESSIONS_JSON=$(curl -sS "$BASE_URL/api/flash/sessions/list")
echo "$SESSIONS_JSON" | python3 -c 'import json,sys; d=json.load(sys.stdin); print("code=", d.get("code"), "count=", len(d.get("data") or []))'
if [ -z "$SESSIONS_ID" ]; then
  SESSIONS_ID=$(json_get "$SESSIONS_JSON" "(obj.get('data') or [{}])[0].get('id')") || {
    echo "no session in list; import mall_flash_seed.sql and set SESSIONS_ID" >&2
    exit 1
  }
fi
echo "sessionsId=$SESSIONS_ID"

echo "==> 2) session goods"
GOODS_JSON=$(curl -sS "$BASE_URL/api/flash/sessions/goods?sessionsId=$SESSIONS_ID")
echo "$GOODS_JSON" | python3 -c 'import json,sys; d=json.load(sys.stdin); print("code=", d.get("code"), "count=", len(d.get("data") or []))'
if [ -z "$GOODS_ID" ]; then
  GOODS_ID=$(json_get "$GOODS_JSON" "(obj.get('data') or [{}])[0].get('goodsId')") || {
    echo "no goods bound to session; bind goods or set GOODS_ID" >&2
    exit 1
  }
fi
echo "goodsId=$GOODS_ID"

echo "==> 3) test JWT (X-Token)"
TOKEN_JSON=$(curl -sS "$BASE_URL/api/basic/getTestToken")
X_TOKEN=$(json_get "$TOKEN_JSON" "obj.get('data')") || {
  echo "getTestToken failed: $TOKEN_JSON" >&2
  exit 1
}
echo "X-Token acquired (len=${#X_TOKEN})"

echo "==> 4) idempotent token (I-token)"
IDEM_JSON=$(curl -sS -H "X-Token: $X_TOKEN" "$BASE_URL/api/order/getIdempotent")
I_TOKEN=$(json_get "$IDEM_JSON" "obj.get('data')") || {
  echo "getIdempotent failed: $IDEM_JSON" >&2
  exit 1
}
echo "I-token acquired"

echo "==> 5) create flash order (async: expect processing message)"
BODY=$(python3 - <<PY
import json
print(json.dumps({
  "sessionsId": int("$SESSIONS_ID"),
  "goodsId": int("$GOODS_ID"),
  "number": int("$NUMBER"),
  "appId": "$APP_ID",
  "deliveryType": 1,
  "receiver": "demo",
  "contact": "13800000000",
  "province": "上海",
  "city": "上海",
  "area": "浦东",
  "address": "demo road 1",
  "purchaseType": "normal"
}))
PY
)
ORDER_JSON=$(curl -sS -X POST "$BASE_URL/api/order/createOrder" \
  -H "Content-Type: application/json" \
  -H "X-Token: $X_TOKEN" \
  -H "I-token: $I_TOKEN" \
  -d "$BODY")
echo "$ORDER_JSON"
CODE=$(json_get "$ORDER_JSON" "obj.get('code')" || echo "")
MSG=$(json_get "$ORDER_JSON" "obj.get('msg')" || echo "")
if [ "$CODE" = "1" ]; then
  echo "OK: createOrder accepted (code=1). MQ consumer should persist order; poll admin/DB if needed."
  exit 0
fi
echo "FAIL: createOrder code=$CODE msg=$MSG" >&2
exit 1
