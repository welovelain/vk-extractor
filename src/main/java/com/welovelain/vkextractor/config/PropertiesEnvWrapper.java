package com.welovelain.vkextractor.config;

import lombok.AllArgsConstructor;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
class PropertiesEnvWrapper {

    private final Properties properties;

    String getProperty(String s) {
        return resolveEnvVars(properties.getProperty(s));
    }

    /*
     * Returns input string with environment variable references expanded, e.g. $SOME_VAR or ${SOME_VAR}
     */
    private static String resolveEnvVars(String input) {
        if (null == input) {
            return null;
        }
        // match ${ENV_VAR_NAME} or $ENV_VAR_NAME
        Pattern p = Pattern.compile("\\$\\{(\\w+)\\}|\\$(\\w+)");
        Matcher m = p.matcher(input); // get a matcher object
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String envVarName = null == m.group(1) ? m.group(2) : m.group(1);
            String envVarValue = System.getenv(envVarName);
            m.appendReplacement(sb, null == envVarValue ? "" : envVarValue);
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
