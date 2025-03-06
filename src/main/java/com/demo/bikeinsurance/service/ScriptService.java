package com.demo.bikeinsurance.service;

import com.demo.bikeinsurance.exception.MissingScriptException;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ScriptService {

    private final GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
    private final ConcurrentHashMap<String, Class<? extends Script>> scriptCache = new ConcurrentHashMap<>();

    public Object executeScript(String scriptName, Map<String, Object> bindings) {
        try {
            // Load or retrieve script class from cache
            Class<? extends Script> scriptClass = scriptCache.computeIfAbsent(scriptName, this::loadScriptClass);
            Script scriptInstance = scriptClass.getDeclaredConstructor().newInstance();

            bindings.forEach(scriptInstance::setProperty);

            return scriptInstance.run();
        } catch (MissingScriptException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Error executing Groovy script: " + scriptName, e);
        }
    }

    // Loads and compiles the Groovy script from the classpath
    private Class<? extends Script> loadScriptClass(String scriptName) {
        String resourcePath = "scripts/" + scriptName;
        ClassPathResource resource = new ClassPathResource(resourcePath);
        if (!resource.exists()) {
            throw new MissingScriptException("Script file not found: " + scriptName);
        }

        try (InputStream inputStream = resource.getInputStream()) {
            String scriptContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            // Compile Groovy script into a Class object
            return groovyClassLoader.parseClass(scriptContent);
        } catch (IOException e) {
            throw new IllegalStateException("Error loading Groovy script file: " + scriptName, e);
        }
    }
}
