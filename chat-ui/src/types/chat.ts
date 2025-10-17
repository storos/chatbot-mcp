export interface Message {
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
  functionsCalled?: FunctionCall[];
}

export interface FunctionCall {
  functionName: string;
  request: Record<string, any>;
  response: any;
}

export interface ChatResponse {
  response: string;
  sessionId: string;
  functionsCalled: FunctionCall[];
}

export interface ChatRequest {
  message: string;
  sessionId: string;
}
