import axios from 'axios';
import { ChatRequest, ChatResponse } from '../types/chat';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8082';

export const chatService = {
  async sendMessage(request: ChatRequest): Promise<ChatResponse> {
    const response = await axios.post<ChatResponse>(
      `${API_BASE_URL}/api/chat`,
      request,
      {
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );
    return response.data;
  },
};
