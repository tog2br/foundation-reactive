package com.foundation.loadtest

import com.foundation.loadtest.client.HttpClient
import com.foundation.loadtest.model.FilmRequest
import com.foundation.loadtest.service.LoadTestService
import com.foundation.loadtest.service.MetricsCollector
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

suspend fun main() {
    println("=== INICIANDO TESTE DE CARGA ===")
    println("Configuração: 200 RPS por 60 segundos")
    println("Servidores: Undertow (8080) e Netty (8081)")
    println("=================================")
    
    val httpClient = HttpClient()
    val metricsCollector = MetricsCollector()
    val loadTestService = LoadTestService(httpClient, metricsCollector)
    
    // Aguardar servidores estarem prontos
    println("Aguardando servidores estarem prontos...")
    delay(5000)
    
    try {
        // Executar teste de carga
        val totalTime = measureTimeMillis {
            loadTestService.runLoadTest()
        }
        
        println("\n=== TESTE CONCLUÍDO ===")
        println("Tempo total: ${totalTime}ms")
        
        // Coletar métricas finais
        loadTestService.collectFinalMetrics()
        
    } catch (e: Exception) {
        println("Erro durante o teste: ${e.message}")
        e.printStackTrace()
    } finally {
        httpClient.close()
    }
}
