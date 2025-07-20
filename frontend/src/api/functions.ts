import axios from 'axios';
import { getToken } from './jwt';

const API_URL = 'http://localhost:8080';

/**
 * 获取用户ID
 * 按优先级从不同来源获取userId
 */
export const getUserId = (): string => {
  // 从localStorage或sessionStorage获取userId
  let userId = localStorage.getItem('userId') || sessionStorage.getItem('userId');

  // 如果userId为空，尝试从其他来源获取
  if (!userId) {
    // 尝试从teacherId获取
    userId = localStorage.getItem('teacherId') || sessionStorage.getItem('teacherId');

    if (!userId) {
      // 如果还是没有，使用默认值
      userId = "2"; // 默认教师ID
      console.warn('无法获取userId，使用默认值2');
    }
  }

  return userId;
};

/**
 * 获取课程教学目标
 * @param courseId 课程ID
 */
export const getCourseObjective = async (courseId: number) => {
  const token = getToken();
  const userId = getUserId();

  try {
    const response = await axios.get(`${API_URL}/teacher/objective/${courseId}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'userId': userId // 添加userId请求头
      }
    });
    return response.data;
  } catch (error) {
    console.error('获取课程教学目标失败', error);
    throw error;
  }
};

/**
 * AI生成课程介绍和教学目标
 * @param courseId 课程ID
 * @param prompt 用户提示
 */
export const generateCourseObjective = async (courseId: number, prompt: string) => {
  const token = getToken();
  const userId = getUserId();
  console.log('发送请求使用的userId:', userId);

  try {
    const response = await axios.post(`${API_URL}/teacher/objective/${courseId}/generate`,
      { prompt },
      {
        headers: {
          'Authorization': `Bearer ${token}`,
          'userId': userId // 添加userId请求头
        }
      }
    );
    return response.data;
  } catch (error) {
    console.error('生成课程介绍和教学目标失败', error);
    throw error;
  }
};

/**
 * 保存课程目标
 * @param courseId 课程ID
 * @param objective 课程目标对象
 */
export const saveCourseObjective = async (courseId: number, objective: any) => {
  const token = getToken();
  const userId = getUserId();

  try {
    const response = await axios.post(`${API_URL}/teacher/objective/${courseId}/save`,
      objective,
      {
        headers: {
          'Authorization': `Bearer ${token}`,
          'userId': userId // 添加userId请求头
        }
      }
    );
    return response.data;
  } catch (error) {
    console.error('保存课程目标失败', error);
    throw error;
  }
};

/**
 * 获取课程教学大纲
 * @param courseId 课程ID
 */
export const getCourseSyllabus = async (courseId: number) => {
  const token = getToken();
  const userId = getUserId();

  try {
    const response = await axios.get(`${API_URL}/teacher/syllabus/${courseId}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'userId': userId
      }
    });
    return response.data;
  } catch (error) {
    console.error('获取课程教学大纲失败', error);
    throw error;
  }
};

/**
 * AI生成教学大纲
 * @param courseId 课程ID
 * @param prompt 用户提示
 */
export const generateCourseSyllabus = async (courseId: number, prompt: string) => {
  const token = getToken();
  const userId = getUserId();

  try {
    const response = await axios.post(`${API_URL}/teacher/syllabus/${courseId}/generate`,
      { prompt },
      {
        headers: {
          'Authorization': `Bearer ${token}`,
          'userId': userId
        }
      }
    );
    return response.data;
  } catch (error) {
    console.error('生成教学大纲失败', error);
    throw error;
  }
};

/**
 * 保存教学大纲
 * @param courseId 课程ID
 * @param syllabus 大纲对象
 */
export const saveCourseSyllabus = async (courseId: number, syllabus: any) => {
  const token = getToken();
  const userId = getUserId();

  try {
    const response = await axios.post(`${API_URL}/teacher/syllabus/${courseId}/save`,
      syllabus,
      {
        headers: {
          'Authorization': `Bearer ${token}`,
          'userId': userId
        }
      }
    );
    return response.data;
  } catch (error) {
    console.error('保存教学大纲失败', error);
    throw error;
  }
};

/**
 * 获取课程讲义
 * @param courseId 课程ID
 */
export const getCourseMaterial = async (courseId: number) => {
  const token = getToken();
  const userId = getUserId();

  try {
    const response = await axios.get(`${API_URL}/teacher/material/${courseId}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'userId': userId
      }
    });
    return response.data;
  } catch (error) {
    console.error('获取课程讲义失败', error);
    throw error;
  }
};

/**
 * AI生成完整课程讲义
 * @param courseId 课程ID
 * @param courseTitle 课程标题
 * @param request 用户请求内容
 */
export const generateCourseMaterial = async (courseId: number, courseTitle: string, request: string) => {
  const token = getToken();
  const userId = getUserId();

  try {
    const response = await axios.post(`${API_URL}/teacher/material/${courseId}/generate`,
      { courseTitle, request },
      {
        headers: {
          'Authorization': `Bearer ${token}`,
          'userId': userId
        }
      }
    );
    return response.data;
  } catch (error) {
    console.error('生成课程讲义失败', error);
    throw error;
  }
};

/**
 * 保存教学讲义
 * @param courseId 课程ID
 * @param material 讲义对象
 */
export const saveCourseMaterial = async (courseId: number, material: any) => {
  const token = getToken();
  const userId = getUserId();

  try {
    const response = await axios.post(`${API_URL}/teacher/material/${courseId}/save`,
      material,
      {
        headers: {
          'Authorization': `Bearer ${token}`,
          'userId': userId
        }
      }
    );
    return response.data;
  } catch (error) {
    console.error('保存课程讲义失败', error);
    throw error;
  }
};
