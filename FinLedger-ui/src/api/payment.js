import api from './index'

export const paymentApi = {
  getAll: () => api.get('/payments'),
  getPending: () => api.get('/payments/pending'),
  getById: (id) => api.get(`/payments/${id}`),
  getByTransaction: (transactionId) => api.get(`/payments/transaction/${transactionId}`),
  create: (data) => api.post('/payments', data),
  approve: (id, approverId) => api.put(`/payments/${id}/approve`, null, { params: { approverId } }),
  reject: (id, approverId) => api.put(`/payments/${id}/reject`, null, { params: { approverId } }),
  complete: (id) => api.put(`/payments/${id}/complete`),
  delete: (id) => api.delete(`/payments/${id}`)
}


