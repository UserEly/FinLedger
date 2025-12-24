import api from './index'

export const entryApi = {
  getAll: () => api.get('/entries'),
  getSubmitted: () => api.get('/entries/submitted'),
  getById: (id) => api.get(`/entries/${id}`),
  getByTransaction: (transactionId) => api.get(`/entries/transaction/${transactionId}`),
  getSplits: (entryId) => api.get(`/entries/${entryId}/splits`),
  create: (data) => api.post('/entries', data),
  updateStatus: (id, status) => api.put(`/entries/${id}/status`, null, { params: { status } }),
  delete: (id) => api.delete(`/entries/${id}`)
}


