/**
 * 权限工具函数
 * 根据用户角色判断权限
 */

/**
 * 检查用户是否有指定角色
 */
export function hasRole(user, role) {
  if (!user || !user.role) return false
  return user.role === role
}

/**
 * 检查是否是会计
 */
export function isAccountant(user) {
  return hasRole(user, 'ACCOUNTANT')
}

/**
 * 检查是否是财务经理
 */
export function isManager(user) {
  return hasRole(user, 'MANAGER')
}

/**
 * 检查是否是老板
 */
export function isBoss(user) {
  return hasRole(user, 'BOSS')
}

/**
 * 检查是否是财务经理或老板
 */
export function isManagerOrBoss(user) {
  return isManager(user) || isBoss(user)
}

/**
 * 检查是否可以创建交易（仅会计）
 */
export function canCreateTransaction(user) {
  return isAccountant(user)
}

/**
 * 检查是否可以创建分录（仅会计）
 */
export function canCreateEntry(user) {
  return isAccountant(user)
}

/**
 * 检查是否可以审核分录（仅财务经理）
 */
export function canReviewEntry(user) {
  return isManager(user)
}

/**
 * 检查是否可以管理账户（仅财务经理）
 */
export function canManageAccount(user) {
  return isManager(user)
}

/**
 * 检查是否可以审批支付（财务经理和老板）
 */
export function canApprovePayment(user) {
  return isManagerOrBoss(user)
}

/**
 * 检查是否可以完成支付（仅老板）
 */
export function canCompletePayment(user) {
  return isBoss(user)
}

/**
 * 检查是否可以查看报表（所有角色）
 */
export function canViewReport(user) {
  return user && user.role
}


