package com.foundation.loadtest.service

import kotlin.math.roundToInt

class MetricsCollector {
    
    private val undertowMetrics = mutableListOf<RequestMetrics>()
    private val nettyMetrics = mutableListOf<RequestMetrics>()
    private val serverThreads = mutableMapOf<String, Int>()
    
    data class RequestMetrics(
        val endpoint: String,
        val responseTime: Long,
        val success: Boolean,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    fun recordRequest(server: String, endpoint: String, responseTime: Long, success: Boolean) {
        val metrics = RequestMetrics(endpoint, responseTime, success)
        when (server) {
            "undertow" -> undertowMetrics.add(metrics)
            "netty" -> nettyMetrics.add(metrics)
        }
    }
    
    fun addServerThreads(server: String, threads: Int) {
        serverThreads[server] = threads
    }
    
    fun clearMetrics(server: String) {
        when (server) {
            "undertow" -> undertowMetrics.clear()
            "netty" -> nettyMetrics.clear()
        }
    }
    
    fun getServerMetrics(server: String): Map<String, Any> {
        val metrics = when (server) {
            "undertow" -> undertowMetrics
            "netty" -> nettyMetrics
            else -> emptyList()
        }
        
        if (metrics.isEmpty()) {
            return mapOf(
                "rps" to 0,
                "avg_latency_ms" to 0,
                "p95_latency_ms" to 0,
                "success_rate" to 0.0,
                "total_requests" to 0
            )
        }
        
        val successfulRequests = metrics.filter { it.success }
        val responseTimes = successfulRequests.map { it.responseTime }
        
        val rps = if (metrics.isNotEmpty()) {
            val timeSpan = (metrics.maxOf { it.timestamp } - metrics.minOf { it.timestamp }) / 1000.0
            if (timeSpan > 0) (metrics.size / timeSpan).roundToInt() else 0
        } else 0
        
        val avgLatency = if (responseTimes.isNotEmpty()) {
            responseTimes.average().roundToInt()
        } else 0
        
        val p95Latency = if (responseTimes.isNotEmpty()) {
            val sorted = responseTimes.sorted()
            val index = (sorted.size * 0.95).toInt()
            sorted[index]
        } else 0
        
        val successRate = if (metrics.isNotEmpty()) {
            (successfulRequests.size.toDouble() / metrics.size * 100)
        } else 0.0
        
        return mapOf(
            "rps" to rps,
            "avg_latency_ms" to avgLatency,
            "p95_latency_ms" to p95Latency,
            "success_rate" to successRate,
            "total_requests" to metrics.size,
            "successful_requests" to successfulRequests.size,
            "failed_requests" to (metrics.size - successfulRequests.size),
            "threads" to (serverThreads[server] ?: 0)
        )
    }
    
    fun printMetrics() {
        println("\n" + "=".repeat(80))
        println("RESULTADOS DO TESTE DE CARGA (50 concorrentes, 30 segundos)")
        println("=".repeat(80))
        
        val undertowStats = getServerMetrics("undertow")
        val nettyStats = getServerMetrics("netty")
        
        // Cabeçalho da tabela
        println()
        println("┌─────────────────┬─────────┬─────────┐")
        println("│     Métrica     │ Undertow│  Netty  │")
        println("├─────────────────┼─────────┼─────────┤")
        
        // RPS
        println("│ RPS             │ ${String.format("%7d", undertowStats["rps"] as Int)} │ ${String.format("%7d", nettyStats["rps"] as Int)} │")
        
        // Latência Média
        println("│ Latência Média  │ ${String.format("%6dms", undertowStats["avg_latency_ms"] as Int)} │ ${String.format("%6dms", nettyStats["avg_latency_ms"] as Int)} │")
        
        // P95 Latência
        println("│ P95 Latência    │ ${String.format("%6dms", undertowStats["p95_latency_ms"] as Int)} │ ${String.format("%6dms", nettyStats["p95_latency_ms"] as Int)} │")
        
        // Taxa de Sucesso
        println("│ Taxa de Sucesso │ ${String.format("%6.1f%%", undertowStats["success_rate"] as Double)} │ ${String.format("%6.1f%%", nettyStats["success_rate"] as Double)} │")
        
        // Total de Requisições
        println("│ Total Req.      │ ${String.format("%7d", undertowStats["total_requests"] as Int)} │ ${String.format("%7d", nettyStats["total_requests"] as Int)} │")
        
        // Requisições Bem-sucedidas
        println("│ Req. Sucesso    │ ${String.format("%7d", undertowStats["successful_requests"] as Int)} │ ${String.format("%7d", nettyStats["successful_requests"] as Int)} │")
        
        // Requisições Falharam
        println("│ Req. Falharam   │ ${String.format("%7d", undertowStats["failed_requests"] as Int)} │ ${String.format("%7d", nettyStats["failed_requests"] as Int)} │")
        
        // Threads
        println("│ Threads         │ ${String.format("%7d", undertowStats["threads"] as Int)} │ ${String.format("%7d", nettyStats["threads"] as Int)} │")
        
        println("└─────────────────┴─────────┴─────────┘")
        
        println("\n=== ANÁLISE COMPARATIVA ===")
        compareMetrics(undertowStats, nettyStats)
    }
    
    private fun compareMetrics(undertow: Map<String, Any>, netty: Map<String, Any>) {
        val undertowRps = undertow["rps"] as Int
        val nettyRps = netty["rps"] as Int
        val rpsDiff = if (undertowRps > 0) ((nettyRps - undertowRps).toDouble() / undertowRps * 100) else 0.0
        
        val undertowLatency = undertow["avg_latency_ms"] as Int
        val nettyLatency = netty["avg_latency_ms"] as Int
        val latencyDiff = if (undertowLatency > 0) ((nettyLatency - undertowLatency).toDouble() / undertowLatency * 100) else 0.0
        
        println("RPS: Netty ${if (rpsDiff > 0) "+" else ""}${String.format("%.1f", rpsDiff)}% vs Undertow")
        println("Latência: Netty ${if (latencyDiff > 0) "+" else ""}${String.format("%.1f", latencyDiff)}% vs Undertow")
        
        when {
            rpsDiff > 10 -> println("🏆 Netty tem performance significativamente melhor em RPS")
            rpsDiff < -10 -> println("🏆 Undertow tem performance significativamente melhor em RPS")
            else -> println("🤝 Performance similar em RPS")
        }
        
        when {
            latencyDiff > 10 -> println("⚠️  Netty tem latência significativamente maior")
            latencyDiff < -10 -> println("🏆 Netty tem latência significativamente menor")
            else -> println("🤝 Latência similar")
        }
    }
}
