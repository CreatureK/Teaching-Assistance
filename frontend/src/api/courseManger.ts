import axios from 'axios';
import { getAuthHeaders, getToken } from './jwt';

const API_URL = 'http://localhost:8080';

// 从JWT令牌中提取用户ID
const getUserId = (): string | null => {
  const token = getToken();
  if (!token) {
    return null;
  }

  try {
    // JWT结构为 header.payload.signature
    const base64Payload = token.split('.')[1];
    // 解码payload
    const payload = JSON.parse(atob(base64Payload));
    // 从payload中获取id
    return payload.id ? String(payload.id) : null;
  } catch (e) {
    console.error('解析JWT令牌失败', e);
    return null;
  }
};

/**
 * 获取教师所有课程
 * @returns 课程列表
 */
export const getTeacherCourses = async () => {
  try {
    const response = await axios.get(`${API_URL}/teacher/courses`, {
      headers: getAuthHeaders()
    });
    return response.data;
  } catch (error) {
    console.error('获取课程列表失败:', error);
    throw error;
  }
};

/**
 * 获取课程详情
 * @param courseId 课程ID
 * @returns 课程详情（包括教学目标、大纲和讲义）
 */
export const getCourseDetail = async (courseId: number) => {
  try {
    const response = await axios.get(`${API_URL}/teacher/course/${courseId}`, {
      headers: getAuthHeaders()
    });
    return response.data;
  } catch (error) {
    console.error('获取课程详情失败:', error);
    throw error;
  }
};

/**
 * 创建新课程
 * @param courseName 课程名称
 * @returns 创建的课程信息
 */
export const createCourse = async (courseName: string) => {
  try {
    const userId = getUserId();
    if (!userId) {
      throw new Error('无法获取用户ID，请重新登录');
    }

    const response = await axios.post(`${API_URL}/teacher/create`,
      { courseName },
      {
        headers: {
          ...getAuthHeaders(),
          'userId': userId // 添加userId到请求头
        }
      }
    );
    return response.data;
  } catch (error) {
    console.error('创建课程失败:', error);
    throw error;
  }
};

/**
 * 更新课程名称
 * @param courseId 课程ID
 * @param courseName 新的课程名称
 * @returns 更新后的课程信息
 */
export const updateCourseName = async (courseId: number, courseName: string) => {
  try {
    const response = await axios.put(`${API_URL}/teacher/course/${courseId}/name`,
      { courseName },
      {
        headers: getAuthHeaders()
      }
    );
    return response.data;
  } catch (error) {
    console.error('更新课程名称失败:', error);
    throw error;
  }
};

/**
 * 删除课程
 * @param courseId 课程ID
 * @returns 删除结果
 */
export const deleteCourse = async (courseId: number) => {
  try {
    const response = await axios.delete(`${API_URL}/teacher/course/${courseId}`, {
      headers: getAuthHeaders()
    });
    return response.data;
  } catch (error) {
    console.error('删除课程失败:', error);
    throw error;
  }
};
