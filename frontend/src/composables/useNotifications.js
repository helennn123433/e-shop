import { useNotification } from 'naive-ui';

export const useNotificationService = () => {
  const notification = useNotification();

  const showNotificationMessage = (type, title, content) => {
    notification[type]({
      title,
      content,
      duration: 3000,
    });
  };

  return { showNotificationMessage };
};
