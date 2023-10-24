package com.atlassian.performance.tools.jiraactions.w3c

import com.atlassian.performance.tools.jiraactions.JsonProviderSingleton.JSON
import com.atlassian.performance.tools.jiraactions.api.w3c.*
import java.time.Duration
import javax.json.Json
import javax.json.JsonArray
import javax.json.JsonObject

internal class VerboseJsonFormat {

    fun serializeRecordedEntries(
        entries: RecordedPerformanceEntries
    ): JsonObject = entries.run {
        JSON.createObjectBuilder()
            .add("navigations", navigations.map { serializeNavigationTiming(it) }.toJsonArray())
            .add("resources", resources.map { serializeResourceTiming(it) }.toJsonArray())
            .add("elements", (elements.map { serializeElementTiming(it) }).toJsonArray())
            .build()
    }

    fun deserializeRecordedEntries(
        json: JsonObject
    ): RecordedPerformanceEntries = json.run {
        RecordedPerformanceEntries(
            navigations = getJsonArray("navigations")
                .map { it.asJsonObject() }
                .map { deserializeNavigationTiming(it) },
            resources = getJsonArray("resources")
                .map { it.asJsonObject() }
                .map { deserializeResourceTiming(it) },
            elements = getJsonArray("elements")
                ?.map { it.asJsonObject() }
                ?.map { deserializeElementTiming(it) }
                ?: emptyList()
        )
    }

    private fun serializeEntry(
        entry: PerformanceEntry
    ): JsonObject = entry.run {
        JSON.createObjectBuilder()
            .add("name", name)
            .add("entryType", entryType)
            .add("startTime", startTime.toString())
            .add("duration", duration.toString())
            .build()
    }

    private fun deserializeEntry(
        json: JsonObject
    ): PerformanceEntry = json.run {
        PerformanceEntry(
            name = getString("name"),
            entryType = getString("entryType"),
            startTime = getDuration("startTime"),
            duration = getDuration("duration")
        )
    }

    private fun serializeResourceTiming(
        resourceTiming: PerformanceResourceTiming
    ): JsonObject = resourceTiming.run {
        JSON.createObjectBuilder()
            .add("entry", serializeEntry(entry))
            .add("initiatorType", initiatorType)
            .add("nextHopProtocol", nextHopProtocol)
            .add("workerStart", workerStart.toString())
            .add("redirectStart", redirectStart.toString())
            .add("redirectEnd", redirectEnd.toString())
            .add("fetchStart", fetchStart.toString())
            .add("domainLookupStart", domainLookupStart.toString())
            .add("domainLookupEnd", domainLookupEnd.toString())
            .add("connectStart", connectStart.toString())
            .add("connectEnd", connectEnd.toString())
            .add("secureConnectionStart", secureConnectionStart.toString())
            .add("requestStart", requestStart.toString())
            .add("responseStart", responseStart.toString())
            .add("responseEnd", responseEnd.toString())
            .add("transferSize", transferSize)
            .add("encodedBodySize", encodedBodySize)
            .add("decodedBodySize", decodedBodySize)
            .apply {
                if (serverTiming != null) {
                    add("serverTiming", serverTiming.map { serializeServerTiming(it) }.toJsonArray())
                }
            }
            .build()
    }

    private fun deserializeResourceTiming(
        json: JsonObject
    ): PerformanceResourceTiming = json.run {
        PerformanceResourceTiming(
            entry = deserializeEntry(getJsonObject("entry")),
            initiatorType = getString("initiatorType"),
            nextHopProtocol = getString("nextHopProtocol"),
            workerStart = getDuration("workerStart"),
            redirectStart = getDuration("redirectStart"),
            redirectEnd = getDuration("redirectEnd"),
            fetchStart = getDuration("fetchStart"),
            domainLookupStart = getDuration("domainLookupStart"),
            domainLookupEnd = getDuration("domainLookupEnd"),
            connectStart = getDuration("connectStart"),
            connectEnd = getDuration("connectEnd"),
            secureConnectionStart = getDuration("secureConnectionStart"),
            requestStart = getDuration("requestStart"),
            responseStart = getDuration("responseStart"),
            responseEnd = getDuration("responseEnd"),
            transferSize = getJsonNumber("transferSize").longValueExact(),
            encodedBodySize = getJsonNumber("encodedBodySize").longValueExact(),
            decodedBodySize = getJsonNumber("decodedBodySize").longValueExact(),
            serverTiming = getJsonArray("serverTiming")
                ?.map { it.asJsonObject() }
                ?.map { deserializeServerTiming(it) }
        )
    }

    private fun serializeNavigationTiming(
        navigationTiming: PerformanceNavigationTiming
    ): JsonObject = navigationTiming.run {
        JSON.createObjectBuilder()
            .add("resource", serializeResourceTiming(resource))
            .add("unloadEventStart", unloadEventStart.toString())
            .add("unloadEventEnd", unloadEventEnd.toString())
            .add("domInteractive", domInteractive.toString())
            .add("domContentLoadedEventStart", domContentLoadedEventStart.toString())
            .add("domContentLoadedEventEnd", domContentLoadedEventEnd.toString())
            .add("domComplete", domComplete.toString())
            .add("loadEventStart", loadEventStart.toString())
            .add("loadEventEnd", loadEventEnd.toString())
            .add("type", type.name)
            .add("redirectCount", redirectCount)
            .build()
    }

    private fun deserializeNavigationTiming(
        json: JsonObject
    ): PerformanceNavigationTiming = json.run {
        PerformanceNavigationTiming(
            resource = deserializeResourceTiming(getJsonObject("resource")),
            unloadEventStart = getDuration("unloadEventStart"),
            unloadEventEnd = getDuration("unloadEventEnd"),
            domInteractive = getDuration("domInteractive"),
            domContentLoadedEventStart = getDuration("domContentLoadedEventStart"),
            domContentLoadedEventEnd = getDuration("domContentLoadedEventEnd"),
            domComplete = getDuration("domComplete"),
            loadEventStart = getDuration("loadEventStart"),
            loadEventEnd = getDuration("loadEventEnd"),
            type = NavigationType.valueOf(getString("type")),
            redirectCount = getJsonNumber("redirectCount").intValueExact()
        )
    }

    private fun serializeServerTiming(
        serverTiming: PerformanceServerTiming
    ): JsonObject = serverTiming.run {
        JSON.createObjectBuilder()
            .add("name", serverTiming.name)
            .add("duration", serverTiming.duration.toString())
            .add("description", serverTiming.description)
            .build()
    }

    private fun deserializeServerTiming(
        json: JsonObject
    ): PerformanceServerTiming = json.run {
        PerformanceServerTiming(
            name = json.getString("name"),
            duration = json.getDuration("duration"),
            description = json.getString("description")
        )
    }

    private fun List<JsonObject>.toJsonArray(): JsonArray {
        val builder = JSON.createArrayBuilder()
        forEach { builder.add(it) }
        return builder.build()
    }

    private fun JsonObject.getDuration(
        field: String
    ): Duration = Duration.parse(getString(field))

    private fun serializeElementTiming(
        elementTiming: PerformanceElementTiming
    ): JsonObject = elementTiming.run {
        return JSON.createObjectBuilder()
            .add("renderTime", renderTime.toString())
            .add("loadTime", loadTime.toString())
            .add("identifier", identifier)
            .add("naturalWidth", naturalWidth)
            .add("naturalHeight", naturalHeight)
            .add("id", id)
            .add("url", url)
            .build()
    }

    private fun deserializeElementTiming(
        json: JsonObject
    ): PerformanceElementTiming = json.run {
        PerformanceElementTiming(
            renderTime = getDuration("renderTime"),
            loadTime = getDuration("loadTime"),
            identifier = getString("identifier"),
            naturalWidth = getJsonNumber("naturalWidth").longValue(),
            naturalHeight = getJsonNumber("naturalHeight").longValue(),
            id = getString("id"),
            url = getString("url")
        )
    }
}
