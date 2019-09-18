package com.tmobile.cso.pacman.qualys.util;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.BaseEncoding;


/**
 * The Class Util.
 */
public class Util {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);
   
    /**
     * Base 64 decode.
     *
     * @param encodedStr the encoded str
     * @return the string
     */
    public static String base64Decode(String encodedStr) {
        try {
            return new String(BaseEncoding.base64().decode(encodedStr), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Error in base64Decode",e);
            return "";
        }
    }
}
