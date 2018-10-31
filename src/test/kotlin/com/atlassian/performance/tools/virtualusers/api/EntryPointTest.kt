package com.atlassian.performance.tools.virtualusers.api

import com.atlassian.performance.tools.virtualusers.ChromeContainer
import com.atlassian.performance.tools.virtualusers.DockerContainers
import com.atlassian.performance.tools.virtualusers.JiraContainer
import com.atlassian.performance.tools.virtualusers.SimpleScenario
import org.junit.Before
import org.junit.Test
import org.testcontainers.containers.Network

class EntryPointTest {
    private val networkName = "entry-point-test-network"

    @Before
    fun before() {
        DockerContainers().clean(networkName)
    }

    @Test
    fun shouldRunWithCustomScenario() {
        Network.NetworkImpl
            .builder()
            .createNetworkCmdModifier { createNetworkCmd ->
                createNetworkCmd.withName(networkName)
            }
            .build()
            .use { network ->
                JiraContainer(
                    network = network
                ).run { jiraAddress ->
                    com.atlassian.performance.tools.virtualusers.api.main(arrayOf(
                        "--jira-address", jiraAddress.toString(),
                        "--login", "admin",
                        "--password", "admin",
                        "--virtual-users", "1",
                        "--hold", "PT0S",
                        "--ramp", "PT0S",
                        "--flat", "PT2M",
                        "--scenario", SimpleScenario::class.java.name,
                        "--browser", ChromeContainer::class.java.name,
                        "--diagnostics-limit", "64",
                        "--seed", "-9183767962456348780"
                    ))
                }
            }
    }
}