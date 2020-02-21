package de.byoc.util;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
@Value.Style(jdkOnly = true,
        of = "new",
        allParameters = true,
        typeAbstract = "*Def", typeImmutable = "*")
public @interface DataConstructorClass {
}
