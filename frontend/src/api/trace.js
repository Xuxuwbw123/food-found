import request from '../utils/request-trace'

// 溯源主表
export const scanTrace = (batchNo) => request.get(`/trace/scan/${batchNo}`)
export const getTraceDetail = (id) => request.get(`/trace/detail/${id}`)
export const getTraceList = (params) => request.get('/trace/list', { params })
export const addTrace = (data) => request.post('/trace/add', data)
export const updateTrace = (data) => request.put('/trace/update', data)
export const deleteTrace = (id) => request.delete(`/trace/delete/${id}`)

// 种植记录
export const getPlanting = (traceId) => request.get(`/trace/planting/${traceId}`)
export const savePlanting = (data) => request.post('/trace/planting/save', data)
export const deletePlanting = (id) => request.delete(`/trace/planting/delete/${id}`)

// 施肥记录
export const getFertilizers = (traceId) => request.get(`/trace/fertilizer/list/${traceId}`)
export const addFertilizer = (data) => request.post('/trace/fertilizer/add', data)
export const updateFertilizer = (data) => request.put('/trace/fertilizer/update', data)
export const deleteFertilizer = (id) => request.delete(`/trace/fertilizer/delete/${id}`)

// 农药记录
export const getPesticides = (traceId) => request.get(`/trace/pesticide/list/${traceId}`)
export const addPesticide = (data) => request.post('/trace/pesticide/add', data)
export const updatePesticide = (data) => request.put('/trace/pesticide/update', data)
export const deletePesticide = (id) => request.delete(`/trace/pesticide/delete/${id}`)

// 灌溉记录
export const getIrrigations = (traceId) => request.get(`/trace/irrigation/list/${traceId}`)
export const addIrrigation = (data) => request.post('/trace/irrigation/add', data)
export const updateIrrigation = (data) => request.put('/trace/irrigation/update', data)
export const deleteIrrigation = (id) => request.delete(`/trace/irrigation/delete/${id}`)

// 养殖记录
export const getBreedings = (traceId) => request.get(`/trace/breeding/list-by-trace/${traceId}`)
export const addBreeding = (data) => request.post('/trace/breeding/add', data)
export const updateBreeding = (data) => request.put('/trace/breeding/update', data)
export const deleteBreeding = (id) => request.delete(`/trace/breeding/delete/${id}`)

// 收获记录
export const getHarvest = (traceId) => request.get(`/trace/harvest/get-by-trace/${traceId}`)
export const addHarvest = (data) => request.post('/trace/harvest/add', data)
export const updateHarvest = (data) => request.put('/trace/harvest/update', data)
export const deleteHarvest = (id) => request.delete(`/trace/harvest/delete/${id}`)

// 加工记录
export const getProcessings = (traceId) => request.get(`/trace/processing/list-by-trace/${traceId}`)
export const addProcessing = (data) => request.post('/trace/processing/add', data)
export const updateProcessing = (data) => request.put('/trace/processing/update', data)
export const deleteProcessing = (id) => request.delete(`/trace/processing/delete/${id}`)

// 检测报告
export const getInspections = (traceId) => request.get(`/trace/inspection/list/${traceId}`)
export const addInspection = (data) => request.post('/trace/inspection/add', data)
export const updateInspection = (data) => request.put('/trace/inspection/update', data)
export const deleteInspection = (id) => request.delete(`/trace/inspection/delete/${id}`)

// 仓储记录
export const getStorage = (traceId) => request.get(`/trace/storage/get-by-trace/${traceId}`)
export const addStorage = (data) => request.post('/trace/storage/add', data)
export const updateStorage = (data) => request.put('/trace/storage/update', data)
export const deleteStorage = (id) => request.delete(`/trace/storage/delete/${id}`)

// 物流信息
export const getLogistics = (traceId) => request.get(`/trace/logistics/${traceId}`)
export const getLogisticsTracks = (logisticsId) => request.get(`/trace/logistics/tracks/${logisticsId}`)
export const saveLogistics = (data) => request.post('/trace/logistics/save', data)
export const saveLogisticsTrack = (data) => request.post('/trace/logistics/track/save', data)
export const deleteLogistics = (id) => request.delete(`/trace/logistics/delete/${id}`)
export const deleteLogisticsTrack = (id) => request.delete(`/trace/logistics/track/delete/${id}`)

// 溯源图片
export const getTraceImages = (traceId) => request.get(`/trace/image/list-by-trace/${traceId}`)
export const addTraceImage = (data) => request.post('/trace/image/add', data)
export const updateTraceImage = (data) => request.put('/trace/image/update', data)
export const deleteTraceImage = (id) => request.delete(`/trace/image/delete/${id}`)

// 农户端
export const getFarmerInfo = (userId) => request.get(`/farmer/info/${userId}`)
export const getFarmerAuthInfo = (userId) => request.get(`/farmer/auth-info/${userId}`)
export const getFarmerDetail = (id) => request.get(`/farmer/detail/${id}`)
export const getFarmerList = (params) => request.get('/farmer/list', { params })
export const applyFarmer = (data) => request.post('/farmer/apply', data)
export const updateFarmer = (data) => request.put('/farmer/update', data)
export const auditFarmer = (id, status, remark) => request.put(`/farmer/audit/${id}?status=${status}&remark=${remark}`)
