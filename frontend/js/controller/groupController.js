import {apiClient, ApiError} from '../model/apiClient.js';
import {getUserId} from '../model/state.js';

const toMessage = (err) => (err instanceof ApiError ? err.message : 'Something went wrong. Please try again.');

export const createGroup = async (name) => {
    const requesterId = getUserId();
    if (!requesterId) return {success: false, error: 'Not logged in'};
    try {
        const group = await apiClient.createGroup(requesterId, name);
        return {success: true, group};
    } catch (err) {
        return {success: false, error: toMessage(err)};
    }
};

export const deleteGroupByName = async (name) => {
    const requesterId = getUserId();
    if (!requesterId) return {success: false, error: 'Not logged in'};
    try {
        await apiClient.deleteGroup(requesterId, name);
        return {success: true};
    } catch (err) {
        return {success: false, error: toMessage(err)};
    }
};

export const updateGroup = async (groupId, {name, description, profileImagePath} = {}) => {
    const requesterId = getUserId();
    if (!requesterId) return {success: false, error: 'Not logged in'};
    try {
        const group = await apiClient.updateGroup(requesterId, groupId, {name, description, profileImagePath});
        return {success: true, group};
    } catch (err) {
        return {success: false, error: toMessage(err)};
    }
};

export const addMember = async (groupId, userId) => {
    const requesterId = getUserId();
    if (!requesterId) return {success: false, error: 'Not logged in'};
    try {
        await apiClient.addGroupMember(groupId, requesterId, userId);
        return {success: true};
    } catch (err) {
        return {success: false, error: toMessage(err)};
    }
};

export const removeMember = async (groupId, userId) => {
    const requesterId = getUserId();
    if (!requesterId) return {success: false, error: 'Not logged in'};
    try {
        await apiClient.removeGroupMember(groupId, requesterId, userId);
        return {success: true};
    } catch (err) {
        return {success: false, error: toMessage(err)};
    }
};

export const leaveGroup = async (groupId) => {
    const userId = getUserId();
    if (!userId) return {success: false, error: 'Not logged in'};
    return removeMember(groupId, userId);
};

export const addAdmin = async (groupId, userId) => {
    const requesterId = getUserId();
    if (!requesterId) return {success: false, error: 'Not logged in'};
    try {
        await apiClient.addGroupAdmin(groupId, requesterId, userId);
        return {success: true};
    } catch (err) {
        return {success: false, error: toMessage(err)};
    }
};

export const removeAdmin = async (groupId, userId) => {
    const requesterId = getUserId();
    if (!requesterId) return {success: false, error: 'Not logged in'};
    try {
        await apiClient.removeGroupAdmin(groupId, requesterId, userId);
        return {success: true};
    } catch (err) {
        return {success: false, error: toMessage(err)};
    }
};

export const getGroupMembers = async (groupId) => {
    try {
        const members = await apiClient.getGroupMembers(groupId);
        return {success: true, members};
    } catch (err) {
        return {success: false, error: toMessage(err)};
    }
};