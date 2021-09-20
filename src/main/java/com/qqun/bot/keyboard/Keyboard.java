package com.qqun.bot.keyboard;

import com.qqun.user.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Keyboard extends InlineKeyboardMarkup {
    public Keyboard(User user) {
        InlineKeyboardButton inventoryButton = new InlineKeyboardButton();
        inventoryButton.setText("Инвентарь \uD83C\uDF92");
        inventoryButton.setCallbackData("openInventory");
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inventoryButton);
        rowList.add(keyboardButtonsRow1);
        setKeyboard(rowList);
    }
}
