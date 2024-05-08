<template>
  <div class="pet-creation">
    <h1>Erstelle dein Haustier</h1>
    <form @submit.prevent="createPet">
      <input id="petName" v-model="petData.name" placeholder="Name des Haustieres" required>
      <select v-model="petData.type" required>
        <option disabled value="">Wähle den Typ</option>
        <option value="Dog">Hund</option>
        <option value="Cat">Katze</option>
      </select>
      <button type="submit">Erstellen</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const petData = ref({
  name: '',
  type: ''
});
const router = useRouter();

const createPet = async () => {
  // Logik zum Erstellen des Haustieres hinzufügen
  console.log('Haustierdaten:', petData.value);
  // Bei Erfolg:
  router.push('/pet');
};
</script>

<style scoped>
.pet-creation {
  max-width: 400px;
  margin: auto;
  padding: 20px;
  box-shadow: 0 0 10px rgba(0,0,0,0.1);
}
</style>