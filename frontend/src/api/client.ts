const API_BASE = import.meta.env.VITE_API_BASE_URL ?? '';

export interface ApiResponse<T> { success: boolean; message: string; data: T; }
export interface Shop { id: number; name: string; category: string; address: string; avgPrice: number; score: number; description: string; }
export interface Voucher { id: number; shopId: number; title: string; payValue: number; actualValue: number; stock: number; beginAt: string; endAt: string; }
export interface SeckillResult { orderId: number; status: string; message: string; }
export interface AgentReply { answer: string; usedTools: string[]; elapsedMs: number; estimatedTokens: number; model: string; }

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...(options?.headers ?? {}) },
    ...options
  });
  const payload = (await response.json()) as ApiResponse<T>;
  if (!payload.success) throw new Error(payload.message);
  return payload.data;
}

export const api = {
  shops: () => request<Shop[]>('/api/shops'),
  vouchers: () => request<Voucher[]>('/api/vouchers'),
  preload: (id: number) => request<void>(`/api/vouchers/${id}/preload`, { method: 'POST' }),
  seckill: (id: number, userId: number) => request<SeckillResult>(`/api/seckill/${id}`, { method: 'POST', headers: { 'X-User-Id': String(userId) } }),
  chat: (question: string) => request<AgentReply>('/api/agent/chat', { method: 'POST', body: JSON.stringify({ userId: 7, sessionId: 'demo-session', question }) })
};
