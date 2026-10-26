package com.platform.mall.web.support;

import com.platform.common.ai.dto.GoodsSnapshot;
import com.platform.mall.dao.goods.model.detail.MallGoodsDetailDto;
import com.platform.mall.dao.goods.model.list.MallGoodsListDto;

/**
 * Map mall goods DTOs into AI GoodsSnapshot (read-only; never used on flash/pay path).
 */
public final class AiGoodsSnapshotFactory {
    private AiGoodsSnapshotFactory() {
    }

    public static GoodsSnapshot fromDetail(MallGoodsDetailDto g) {
        if (g == null) {
            return null;
        }
        return GoodsSnapshot.builder()
                .id(g.getId())
                .name(g.getName())
                .title(g.getTitle())
                .price(g.getPrice())
                .proposedPrice(g.getProposedPrice())
                .stock(g.getStock())
                .stockWarn(g.getStockWarn())
                .onUnSale(g.getOnUnSale())
                .build();
    }

    public static GoodsSnapshot fromList(MallGoodsListDto g) {
        if (g == null) {
            return null;
        }
        return GoodsSnapshot.builder()
                .id(g.getId())
                .name(g.getName())
                .title(g.getTitle())
                .price(g.getPrice())
                .proposedPrice(g.getProposedPrice())
                .stock(g.getStock())
                .stockWarn(g.getStockWarn())
                .onUnSale(g.getOnUnSale())
                .build();
    }
}
