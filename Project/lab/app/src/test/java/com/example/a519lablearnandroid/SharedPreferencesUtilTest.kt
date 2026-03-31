package com.example.a519lablearnandroid

import android.content.Context
import android.content.SharedPreferences
import com.example.a519lablearnandroid.Util.SharedPreferencesUtil
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class SharedPreferencesUtilTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockPrefs: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs)
        `when`(mockPrefs.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), any())).thenReturn(mockEditor)
        `when`(mockEditor.putInt(anyString(), anyInt())).thenReturn(mockEditor)
        `when`(mockEditor.putBoolean(anyString(), any())).thenReturn(mockEditor)
        `when`(mockEditor.remove(anyString())).thenReturn(mockEditor)
        `when`(mockEditor.clear()).thenReturn(mockEditor)
        
        SharedPreferencesUtil.init(mockContext)
    }

    @Test
    fun testSaveAndGetString() {
        val key = "testKey"
        val value = "testValue"
        
        `when`(mockPrefs.getString(key, "")).thenReturn(value)
        
        SharedPreferencesUtil.saveString(key, value)
        verify(mockEditor).putString(key, value)
        verify(mockEditor).apply()
        
        val result = SharedPreferencesUtil.getString(key)
        assertEquals(value, result)
    }

    @Test
    fun testSaveAndGetInt() {
        val key = "testInt"
        val value = 42
        
        `when`(mockPrefs.getInt(key, 0)).thenReturn(value)
        
        SharedPreferencesUtil.saveInt(key, value)
        verify(mockEditor).putInt(key, value)
        
        val result = SharedPreferencesUtil.getInt(key)
        assertEquals(value, result)
    }

    @Test
    fun testRemove() {
        val key = "toRemove"
        SharedPreferencesUtil.remove(key)
        verify(mockEditor).remove(key)
        verify(mockEditor).apply()
    }

    @Test
    fun testClearAll() {
        SharedPreferencesUtil.clearAll()
        verify(mockEditor).clear()
        verify(mockEditor).apply()
    }
}
