package org.example;


import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.runtime.Startup;
import jakarta.inject.Inject;

@Startup
public class Some {

    @Inject
    KubernetesClient client;
}
