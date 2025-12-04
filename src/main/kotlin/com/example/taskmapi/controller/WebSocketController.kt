import com.example.taskmapi.dto.TaskResponse
import com.example.taskmapi.dto.TaskUpdateMessage
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class WebSocketController(private val template: SimpMessagingTemplate) {

    fun broadcastTasks(boardId: Long, tasks: List<TaskResponse>) {
        template.convertAndSend("/topic/board.$boardId", tasks)
    }

    fun sendTaskUpdate(boardId: Long, message: TaskUpdateMessage) {
        template.convertAndSend("/topic/board/$boardId", message)
    }
}
