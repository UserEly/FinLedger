import api from './index'

export const transactionApi = {
  getAll: () => api.get('/transactions'),
  getPending: () => api.get('/transactions/pending'),
  getById: (id) => api.get(`/transactions/${id}`),
  getByUser: (userId) => api.get(`/transactions/user/${userId}`),
  create: (data) => api.post('/transactions', data),
  update: (id, data) => api.put(`/transactions/${id}`, data),
  delete: (id) => api.delete(`/transactions/${id}`)
}


