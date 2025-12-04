import { io, Socket } from "socket.io-client";
import type { TaskResponse } from "@/types/dto";
import { reactive } from "vue";

class SocketServiceClass {
  private socket: Socket | null = null;

  // reactive storage for tasks per board
  public tasks = reactive<Record<number, TaskResponse[]>>({});

  connect(token?: string) {
    if (this.socket && this.socket.connected) return;

    this.socket = io("http://localhost:9092", {
      auth: { token }, // optional JWT token for auth
      reconnectionAttempts: 5,
    });

    this.socket.on("connect", () => {
      console.log("Socket.IO connected:", this.socket?.id);
    });

    this.socket.on("disconnect", (reason) => {
      console.log("Socket.IO disconnected:", reason);
    });
  }

  subscribeToBoard(boardId: number) {
    if (!this.socket) return;

    // join board room
    this.socket.emit("joinBoard", boardId);

    // listen for task updates for this board
    this.socket.on(`board:${boardId}:tasks`, (updatedTasks: TaskResponse[]) => {
      this.tasks[boardId] = updatedTasks;
    });
  }

  emitTaskUpdate(boardId: number, tasks: TaskResponse[]) {
    if (!this.socket) return;
    this.socket.emit(`board:${boardId}:tasks:update`, tasks);
  }

  disconnect() {
    this.socket?.disconnect();
    this.socket = null;
  }
}

// single instance
export const SocketService = new SocketServiceClass();
