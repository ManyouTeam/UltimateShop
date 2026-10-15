package cn.superiormc.ultimateshop.gui.arc;

import cn.superiormc.ultimateshop.gui.inv.SearchGUI;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.buttons.ObjectSearchResultButton;
import cn.superiormc.ultimateshop.objects.menus.ObjectSearchMenu;

final class ArcSearchGUI extends ArcCommonGUI {
    ArcSearchGUI(SearchGUI source, ArcMenuRuntime runtime) {
        super(source, runtime);
    }

    @Override
    protected ObjectItem product(int slot) {
        return buttons.get(slot) instanceof ObjectSearchResultButton result ? result.getItem() : super.product(slot);
    }

    @Override
    protected boolean shouldRenderSlot(int slot) {
        return !(getMenu() instanceof ObjectSearchMenu search && search.getInputSlots().contains(slot));
    }

    @Override
    protected boolean isProductCard(int slot) {
        return super.isProductCard(slot) || buttons.get(slot) instanceof ObjectSearchResultButton;
    }
}
