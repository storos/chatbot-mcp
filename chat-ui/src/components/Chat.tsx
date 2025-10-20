import React, { useState, useEffect, useRef } from 'react';
import { Message } from '../types/chat';
import { chatService } from '../services/chatService';
import '../styles/Chat.css';

const Chat: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [sessionId] = useState(() => `session-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`);
  const [error, setError] = useState<string | null>(null);
  const [expandedFunctions, setExpandedFunctions] = useState<Set<string>>(new Set());
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!inputValue.trim() || isLoading) {
      return;
    }

    const userMessage: Message = {
      role: 'user',
      content: inputValue,
      timestamp: new Date(),
    };

    setMessages(prev => [...prev, userMessage]);
    setInputValue('');
    setIsLoading(true);
    setError(null);

    try {
      const response = await chatService.sendMessage({
        message: inputValue,
        sessionId: sessionId,
      });

      const assistantMessage: Message = {
        role: 'assistant',
        content: response.response,
        timestamp: new Date(),
        functionsCalled: response.functionsCalled,
      };

      setMessages(prev => [...prev, assistantMessage]);
    } catch (err) {
      console.error('Error sending message:', err);
      setError('Mesaj gönderilemedi. Lütfen tekrar deneyin.');

      const errorMessage: Message = {
        role: 'assistant',
        content: 'Üzgünüm, bir hata oluştu. Lütfen tekrar deneyin.',
        timestamp: new Date(),
      };

      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  const formatTime = (date: Date) => {
    return date.toLocaleTimeString('tr-TR', {
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const toggleFunctionExpand = (messageIndex: number, functionIndex: number) => {
    const key = `${messageIndex}-${functionIndex}`;
    setExpandedFunctions(prev => {
      const newSet = new Set(prev);
      if (newSet.has(key)) {
        newSet.delete(key);
      } else {
        newSet.add(key);
      }
      return newSet;
    });
  };

  const isFunctionExpanded = (messageIndex: number, functionIndex: number) => {
    return expandedFunctions.has(`${messageIndex}-${functionIndex}`);
  };

  return (
    <div className="chat-container">
      <div className="chat-header">
        <h1>E-Ticaret Müşteri Destek</h1>
        <div className="session-id">Oturum: {sessionId}</div>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      <div className="messages-container">
        {messages.length === 0 && (
          <div style={{
            textAlign: 'center',
            color: '#95a5a6',
            marginTop: '40px',
            fontSize: '16px'
          }}>
            Merhaba! Size nasıl yardımcı olabilirim?
          </div>
        )}

        {messages.map((message, index) => (
          <div key={index} className={`message ${message.role}`}>
            {message.role === 'assistant' && (
              <div className="message-avatar">AI</div>
            )}

            <div className="message-content">
              {message.content}
              <div className="message-timestamp">
                {formatTime(message.timestamp)}
              </div>

              {message.functionsCalled && message.functionsCalled.length > 0 && (
                <div className="function-calls">
                  <strong>Çağrılan Fonksiyonlar:</strong>
                  {message.functionsCalled.map((func, funcIdx) => {
                    const isExpanded = isFunctionExpanded(index, funcIdx);
                    return (
                      <div key={funcIdx} className="function-call-item">
                        <div
                          className="function-call-header"
                          onClick={() => toggleFunctionExpand(index, funcIdx)}
                        >
                          <span className="function-expand-icon">
                            {isExpanded ? '▼' : '▶'}
                          </span>
                          <span className="function-name">
                            {func.functionName}
                          </span>
                        </div>

                        {isExpanded && (
                          <div className="function-call-details">
                            <div className="function-detail-section">
                              <div className="function-detail-label">Request:</div>
                              <pre className="function-detail-content">
                                {JSON.stringify(func.request, null, 2)}
                              </pre>
                            </div>

                            <div className="function-detail-section">
                              <div className="function-detail-label">Response:</div>
                              <pre className="function-detail-content">
                                {JSON.stringify(func.response, null, 2)}
                              </pre>
                            </div>
                          </div>
                        )}
                      </div>
                    );
                  })}
                </div>
              )}
            </div>

            {message.role === 'user' && (
              <div className="message-avatar">SEN</div>
            )}
          </div>
        ))}

        {isLoading && (
          <div className="message assistant">
            <div className="message-avatar">AI</div>
            <div className="loading-indicator">
              <div className="loading-dots">
                <span></span>
                <span></span>
                <span></span>
              </div>
              <span>Yazıyor...</span>
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      <div className="input-container">
        <form onSubmit={handleSubmit} className="input-form">
          <input
            type="text"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            placeholder="Mesajınızı yazın..."
            disabled={isLoading}
          />
          <button type="submit" disabled={isLoading || !inputValue.trim()}>
            Gönder
          </button>
        </form>
      </div>
    </div>
  );
};

export default Chat;
