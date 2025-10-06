package com.foundation.undertow.service

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.stereotype.Service
import java.lang.management.ManagementFactory
import java.lang.management.MemoryMXBean
import java.lang.management.ThreadMXBean
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.roundToInt

@Service
class MetricsService(private val meterRegistry: MeterRegistry) {
    
    private val requestCounter: Counter = Counter.builder("http_requests_total")
        .description("Total HTTP requests")
        .register(meterRegistry)
    
    private val requestTimer: Timer = Timer.builder("http_request_duration")
        .description("HTTP request duration")
        .register(meterRegistry)
    
    private val responseTimes = ConcurrentHashMap<String, MutableList<Long>>()
    private val startTime = AtomicLong(System.currentTimeMillis())
    private val requestCount = AtomicLong(0)
    
    private val memoryBean: MemoryMXBean = ManagementFactory.getMemoryMXBean()
    private val threadBean: ThreadMXBean = ManagementFactory.getThreadMXBean()
    
    fun recordRequest(endpoint: String, duration: Long) {
        requestCounter.increment()
        requestTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS)
        
        responseTimes.computeIfAbsent(endpoint) { mutableListOf() }.add(duration)
        requestCount.incrementAndGet()
    }
    
    fun getMetrics(): Map<String, Any> {
        val currentTime = System.currentTimeMillis()
        val elapsedSeconds = (currentTime - startTime.get()) / 1000.0
        
        val rps = if (elapsedSeconds > 0) {
            (requestCount.get() / elapsedSeconds).roundToInt()
        } else 0
        
        val allResponseTimes = responseTimes.values.flatten()
        val avgLatency = if (allResponseTimes.isNotEmpty()) {
            allResponseTimes.average().roundToInt()
        } else 0
        
        val p95Latency = if (allResponseTimes.isNotEmpty()) {
            val sorted = allResponseTimes.sorted()
            val index = (sorted.size * 0.95).toInt()
            sorted[index]
        } else 0
        
        val memoryMB = memoryBean.heapMemoryUsage.used / (1024 * 1024)
        val cpuUsage = getCpuUsage()
        val threadCount = threadBean.threadCount
        
        return mapOf(
            "rps" to rps,
            "avg_latency_ms" to avgLatency,
            "p95_latency_ms" to p95Latency,
            "memory_mb" to memoryMB,
            "cpu_percent" to cpuUsage,
            "threads" to threadCount,
            "total_requests" to requestCount.get(),
            "elapsed_seconds" to elapsedSeconds
        )
    }
    
    private fun getCpuUsage(): Double {
        val runtime = Runtime.getRuntime()
        val processors = runtime.availableProcessors()
        
        // Simple CPU usage estimation based on memory and thread usage
        val memoryUsage = memoryBean.heapMemoryUsage.used.toDouble() / memoryBean.heapMemoryUsage.max
        val threadUsage = threadBean.threadCount.toDouble() / (processors * 2)
        
        return ((memoryUsage + threadUsage) / 2 * 100).roundToInt().toDouble()
    }
    
    fun logMetrics() {
        val metrics = getMetrics()
        println("=== MÉTRICAS DO SERVIDOR UNDERPOW ===")
        println("RPS: ${metrics["rps"]}")
        println("Latência Média: ${metrics["avg_latency_ms"]}ms")
        println("P95 Latência: ${metrics["p95_latency_ms"]}ms")
        println("Memória: ${metrics["memory_mb"]}MB")
        println("CPU Médio: ${metrics["cpu_percent"]}%")
        println("Threads: ${metrics["threads"]}")
        println("Total de Requisições: ${metrics["total_requests"]}")
        println("Tempo Decorrido: ${metrics["elapsed_seconds"]}s")
        println("=====================================")
    }
}
