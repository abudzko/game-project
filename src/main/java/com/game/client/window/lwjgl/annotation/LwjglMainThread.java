package com.game.client.window.lwjgl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark methods which should be called from main thread only
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.SOURCE)
public @interface LwjglMainThread {
}
