package com.ebridgecommerce.sdp.domain;

import com.ebridgecommerce.sdp.util.Validator;

import java.util.Map;

public class UssdMenuItem {

    private StatusDTO menuId;
    private String menuText;
    private String errorText;
    private String blankErrorText;
    private Validator validator;

    public UssdMenuItem( StatusDTO menuId,
                         String menuText,
                         String errorText,
                         String blankErrorText,
                         Validator validator) {
        this.menuId = menuId;
        this.menuText = menuText;
        this.errorText = errorText;
        this.blankErrorText = blankErrorText;
        this.validator = validator;
    }

    public StatusDTO getMenuId() {
        return menuId;
    }

    public void setMenuId(StatusDTO menuId) {
        this.menuId = menuId;
    }

    public String getMenuText() {
        return menuText;
    }

    public void setMenuText(String menuText) {
        this.menuText = menuText;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public String getBlankErrorText() {
        return blankErrorText;
    }

    public void setBlankErrorText(String blankErrorText) {
        this.blankErrorText = blankErrorText;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }
}
