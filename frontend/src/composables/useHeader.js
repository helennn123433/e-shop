import { ref } from 'vue';

export function useDrawer() {
  const isDrawerVisible = ref(false);

  const toggleDrawer = () => {
    isDrawerVisible.value = !isDrawerVisible.value;
  };

  const closeDrawer = () => {
    isDrawerVisible.value = false;
  };

  return {
    isDrawerVisible,
    toggleDrawer,
    closeDrawer
  };
}