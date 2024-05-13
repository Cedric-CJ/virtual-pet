<template>
  <div class="pet-creation">
    <h1>Erstelle dein Haustier</h1>
    <form @submit.prevent="createPet">
      <div class="pet-parts">
        <div class="pet-part" v-for="(part, index) in parts" :key="index">
          <button class="nav-button up" @click="changePart(index, -1)">↑</button>
          <img :src="part.options[part.current]" :alt="`Pet ${part.name}`" />
          <button class="nav-button down" @click="changePart(index, 1)">↓</button>
        </div>
      </div>
      <input id="petName" v-model="petData.name" placeholder="Name des Haustieres" required>
      <button type="submit">Erstellen</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const parts = ref([
  { name: 'head', options: ['/path/to/dog_head1.png', '/path/to/dog_head2.png'], current: 0 },
  { name: 'body', options: ['/path/to/dog_body1.png', '/path/to/dog_body2.png'], current: 0 },
  { name: 'tail', options: ['/path/to/dog_tail1.png', '/path/to/dog_tail2.png'], current: 0 }
]);

const petData = ref({
  name: '',
  type: ''
});
const router = useRouter();

const createPet = () => {
  console.log('Haustierdaten:', petData.value);
  router.push('/pet');
};

const changePart = (index, direction) => {
  const part = parts.value[index];
  part.current += direction;
  if (part.current >= part.options.length) {
    part.current = 0;
  } else if (part.current < 0) {
    part.current = part.options.length - 1;
  }
};
</script>
<style scoped>
.pet-creation {
  max-width: 600px;
  margin: auto;
  padding: 20px;
  text-align: center;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.pet-parts {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}

.pet-part {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 10px;
}

.nav-button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 24px;
  padding: 10px;
  color: white; /* Weiße Pfeile */
}

.image-slot {
  width: 120px; /* Anpassen nach Bedarf */
  height: 120px; /* Anpassen nach Bedarf */
  display: flex;
  justify-content: center;
  align-items: center;
  background-size: cover;
  background-position: center;
}

.pet-part img {
  max-width: 100px;
  max-height: 100px;
}

.actions button {
  padding: 10px 15px;
  border: 1px solid #ccc;
  margin-top: 20px;
}
</style>