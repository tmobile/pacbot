package com.tmobile.cso.pacman.datashipper.util;

import org.junit.Assert;
import org.junit.Test;
import java.util.HashMap;

public class UtilTest {

    @Test
    public void testContains() {
        HashMap<String, String> hash1 = new HashMap<>();
        HashMap<String, String> hash2 = new HashMap<>();
        hash2.put("foo", "3");

        Assert.assertFalse(Util.contains(hash2, hash1, new String[]{"foo"}));

        hash1.put("foo", "3");
        Assert.assertTrue(Util.contains(hash2, hash1, new String[]{"foo"}));
    }

    @Test
    public void testConcatenate() {
        HashMap<String, Object> hash = new HashMap<>();
        hash.put("foo", "3");
        hash.put("bar", "4");
        hash.put("baz", "5");

        Assert.assertEquals("3",
                Util.concatenate(hash, new String[]{"foo"}, ","));
        Assert.assertEquals("4,5",
                Util.concatenate(hash, new String[]{"bar", "baz"}, ","));
    }

    @Test
    public void testParseJson() {
        String json = "{\"id\": \"1\",\"name\":\"Julie Sherman\"}";

        HashMap<String, String> hashMap1 = new HashMap<>();
        hashMap1.put("id", "1");
        hashMap1.put("name", "Julie Sherman");

        Assert.assertEquals(hashMap1, Util.parseJson(json));
        Assert.assertEquals(new HashMap<>(), Util.parseJson("foo"));
    }

    @Test
    public void testGetUniqueID() {
        Assert.assertEquals("ACBD18DB4CC2F85CEDEF654FCCC4A4D8",
                Util.getUniqueID("foo"));
    }

    @Test
    public void testGetStackTrace() {
        Assert.assertNotNull(Util.getStackTrace(new NullPointerException()));
    }

    @Test
    public void testBase64Decode() {
        Assert.assertEquals("foo", Util.base64Decode("Zm9v"));
    }

    @Test
    public void testBase64Encode() {
        Assert.assertEquals("Zm9v", Util.base64Encode("foo"));
    }

    @Test
    public void testEncodeUrl() {
        Assert.assertEquals("http%3A%2F%2Fbbc.co.uk",
                Util.encodeUrl("http://bbc.co.uk"));
    }

    @Test
    public void testGetHeader() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Authorization", "Basic foo");
        hashMap.put("Content-Type", "application/json; charset=UTF-8");

        Assert.assertEquals(hashMap, Util.getHeader("foo"));
    }
}
