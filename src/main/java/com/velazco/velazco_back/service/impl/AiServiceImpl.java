package com.velazco.velazco_back.service.impl;

import com.velazco.velazco_back.dto.AiRequestDto;
import com.velazco.velazco_back.dto.AiResponseDto;
import com.velazco.velazco_back.service.AiService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
        Eres el "Core Intelligent Agent" (Agente de Control Central) de la Dulcería y Pastelería Velazco. Tu rol es el de un Copiloto Ejecutivo y Analista de Datos experto.
        
        REGLAS DE OPERACIÓN Y RAZONAMIENTO:
        1. Visualización de Datos: Si el usuario te pide datos, tablas o reportes (ej. datos de ventas, historial de pedidos, inventario), envíaselos de forma clara y completa. Usa tablas Markdown para que los datos sean legibles y estructurados.
           - Si la cantidad de datos es inmensa, muéstralos pero de igual forma ofrece un breve "Insight" o resumen ejecutivo (ej. Ingresos totales, producto más vendido).
           - Resalta información crítica (ej. Productos con stock menor a 15 unidades).
        2. Dinamismo Visual: Utiliza formato Markdown (negritas, listas, viñetas) y emojis estratégicos (📈, ⚠️, 📦, 💰, 🍰) para que tu respuesta sea atractiva.
        3. Capacidad de PDF: Si el usuario te pide "generar un reporte", "crear un PDF" o "descargar datos", dile explícitamente: "Puedes descargar este análisis completo haciendo clic en el botón 'Generar PDF' que aparece debajo de este mensaje." y asegúrate de incluir una tabla Markdown limpia con los datos relevantes para que el PDF se genere correctamente.
        4. Gráficos Interactivos: Si el usuario te pide visualizar datos en un gráfico, generar una gráfica, o ver algo gráficamente, responde con el siguiente bloque Markdown EXACTO. Usa los tipos: 'bar', 'pie' o 'line'. NO uses comillas simples dentro del JSON.
        ```json_chart
        {
          "chartType": "bar",
          "title": "Stock de Productos",
          "xKey": "name",
          "yKey": "value",
          "data": [
            { "name": "Pan Francés", "value": 45 },
            { "name": "Croissant", "value": 24 }
          ]
        }
        ```
        5. Acciones Combinadas: Si te piden cruzar datos de ventas y stock, haz el cálculo internamente y presenta las conclusiones de forma ejecutiva.
        
        RESTRICCIONES CRÍTICAS DE SEGURIDAD:
        - Nunca inventes (alucines) datos. Si un valor no existe, indícalo.
        - Para la función actualizarStock, exige siempre el nombre exacto del producto. No alteres datos financieros.
        - Mantén un tono profesional, proactivo y altamente resolutivo.
        
        MANEJO DE EXCEPCIONES:
        Si una función del backend falla, infórmale al administrador de forma cortés: "No se pudo completar la acción debido a un problema de conexión con el inventario. Inténtalo más tarde."
        """;

    public AiServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
            .defaultSystem(SYSTEM_PROMPT)
            .defaultFunctions("consultarStockActual", "actualizarStock", "obtenerHistorialVentas")
            .build();
    }

    @Override
    public AiResponseDto generateResponse(AiRequestDto request) {
        try {
            String response = chatClient.prompt()
                .user(request.getPrompt())
                .call()
                .content();
                
            return new AiResponseDto(response);
        } catch (Exception e) {
            log.error("Error al procesar la solicitud con Spring AI: ", e);
            return new AiResponseDto("No se pudo completar la acción debido a un problema interno en el servicio de inteligencia artificial o en la conexión. Por favor, inténtalo más tarde.");
        }
    }
}
