package com.demo.bikeinsurance.service;

import com.demo.bikeinsurance.exception.MissingScriptException;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;
import groovy.lang.Script;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Service
public class ScriptService {

    private final GroovyShell groovyShell = new GroovyShell();

    public Object executeScript(String scriptName, Map<String, Object> bindings) {
        ClassPathResource resource = new ClassPathResource("scripts/" + scriptName);
        if (!resource.exists()) {
            throw new MissingScriptException("Script file not found: " + scriptName);
        }

        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
            Script script = groovyShell.parse(reader);
            bindings.forEach(script::setProperty);

            return script.run();
        } catch (MissingPropertyException e) {
            throw new IllegalArgumentException("A required property is missing in script: " + scriptName, e);
        } catch (IOException e) {
            throw new IllegalStateException("Error reading Groovy script file: " + scriptName, e);
        } catch (Exception e) {
            throw new IllegalStateException("Error executing Groovy script: " + scriptName, e);
        }
    }
}
