package kim.hhhhhy.mock.care

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.files
import io.ktor.server.http.content.static
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.io.File

/**
 * HTTP服务器类，负责启动和配置Web服务器
 */
class CareServer {
    /**
     * 启动服务器
     */
    fun start(port: Int) {
        embeddedServer(
            factory = Netty,
            port = port,
            host = AppConfig.HOST
        ) {
            configureRouting()
        }.start(wait = true)
    }
    
    /**
     * 配置路由
     */
    private fun Application.configureRouting() {
        routing {
            get("/") {
                call.respondText(
                    text = HtmlGenerator.createHtmlPage(),
                    contentType = io.ktor.http.ContentType.Text.Html
                )
            }
            
            // 配置静态资源路由，允许直接访问根目录下的文件
            static("/") {
                // 从当前工作目录提供静态文件
                files(File("."))
            }
        }
    }
}