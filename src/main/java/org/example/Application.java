package org.example;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("api")
public class Application extends jakarta.ws.rs.core.Application {
    // Needed to enable Jakarta REST and specify a path.
}
