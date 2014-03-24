package com.ebridgecommerce.sdp.domain;

import com.ebridgecommerce.sdp.util.Util;

public enum StatusDTO {
    MAIN,
    PROMPT_FIRSTNAME,
    PROMPT_LASTNAME,
    PROMPT_IDNUMBER,
    PROMPT_PHYSICALADDRESS,
    CAPTURED,
    REGISTERED,
    SAF;

    public static StatusDTO fromString(String description) {
        return Util.getEnumFromString(StatusDTO.class, description);
    }

    public static StatusDTO next(StatusDTO o) {
        int index = indexOf( o );
        return index == -1 ? null : itemAt(++index);
    }

    public static int indexOf(StatusDTO o) {
        int idx = 0;
        for ( StatusDTO statusDTO : values() ) {
            if (o == statusDTO) {
                return idx;
            }
            ++idx;
        }
        return -1;
    }

    private static StatusDTO itemAt(int idx) {
        return idx >= values().length ? null : values()[idx];
    }

    public static boolean isLast(StatusDTO o) {
        return indexOf(o) == (values().length - 1);
    }

}
