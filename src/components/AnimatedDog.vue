<template>
  <div class="animated-dog">
    <!-- Der Wrapper; in Vue vermeide den <body>-Tag -->
    <div class="wrapper" ref="wrapper">
      <!-- Marker (optional – zum Debuggen: d-none ausblenden) -->
      <div class="marker red d-none"></div>
      <div class="marker green d-none"></div>
      <div class="marker blue d-none"></div>

      <!-- Hund-Struktur (Grundkonfiguration, wie in Deinem HTML) -->
      <div class="dog" ref="dog">
        <div class="body-wrapper">
          <div class="body img-bg"></div>
        </div>
        <div class="head-wrapper">
          <div class="head img-bg"></div>
        </div>
        <div class="leg-wrapper">
          <div class="leg one img-bg"></div>
        </div>
        <div class="leg-wrapper">
          <div class="leg two img-bg"></div>
        </div>
        <div class="leg-wrapper">
          <div class="leg three img-bg"></div>
        </div>
        <div class="leg-wrapper">
          <div class="leg four img-bg"></div>
        </div>
        <div class="tail-wrapper">
          <div class="tail img-bg"></div>
        </div>
      </div>
    </div>

    <!-- Steuerungsknöpfe zum Auslösen der Aktionen -->
    <div class="controls">
      <button @click="makeDogJump">Springen</button>
      <button @click="makeDogSit">Sitzen</button>
      <button @click="makeDogLieDown">Leg dich</button>
      <button @click="makeDogDrink">Trinken</button>
      <button @click="makeDogEat">Essen</button>
      <button @click="makeDogSleep">Schlafen</button>
      <button @click="makeDogShower">Duschen</button>
      <button @click="makeDogPlay">Spielen</button>
    </div>

    <div class="indicator"></div>
    <div class="sign">
      by masahito / <a href="http://www.ma5a.com/">ma5a.com</a>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

/*
  Wir legen zwei Refs für den Wrapper und den Hund an, sodass wir
  direkt auf diese DOM-Elemente zugreifen können – so vermeiden wir
  Probleme mit zusätzlichen Knoten, die durch Vue entstehen.
*/
const wrapper = ref(null);
const dog = ref(null);

/* Flag, um während einer Aktion (z. B. Animation) die Mausverfolgung zu deaktivieren */
let isActionActive = false;

/* Hilfsfunktion, um Zahlen in Pixelstrings umzuwandeln */
const px = num => `${num}px`;

function init() {
  // Wir arbeiten mit den Elementen, die über die Refs verfügbar sind
  const elements = {
    wrapper: wrapper.value,
    dog: dog.value,
    marker: wrapper.value.querySelectorAll('.marker')
  };

  // Alle wichtigen Parameter – diese kannst Du später noch anpassen
  const animationFrames = {
    rotate: [[0], [1], [2], [3], [5], [3, 'f'], [2, 'f'], [1, 'f']]
  };
  const directionConversions = {
    360: 'up',
    45: 'upright',
    90: 'right',
    135: 'downright',
    180: 'down',
    225: 'downleft',
    270: 'left',
    315: 'upleft'
  };
  const angles = [360, 45, 90, 135, 180, 225, 270, 315];
  const defaultEnd = 4;
  // Hier definieren wir Positionen für die vier Beine und den Schwanz – diese Werte können angepasst werden
  const partPositions = [
    { leg1: { x: 26, y: 43 }, leg2: { x: 54, y: 43 }, leg3: { x: 26, y: 75 }, leg4: { x: 54, y: 75 }, tail: { x: 40, y: 70, z: 1 } },
    { leg1: { x: 33, y: 56 }, leg2: { x: 55, y: 56 }, leg3: { x: 12, y: 72 }, leg4: { x: 32, y: 74 }, tail: { x: 20, y: 64, z: 1 } },
    { leg1: { x: 59, y: 62 }, leg2: { x: 44, y: 60 }, leg3: { x: 25, y: 64 }, leg4: { x: 11, y: 61 }, tail: { x: 4, y: 44, z: 1 } },
    { leg1: { x: 39, y: 63 }, leg2: { x: 60, y: 56 }, leg3: { x: 12, y: 52 }, leg4: { x: 28, y: 50 }, tail: { x: 7, y: 21, z: 0 } },
    { leg1: { x: 23, y: 54 }, leg2: { x: 56, y: 54 }, leg3: { x: 24, y: 25 }, leg4: { x: 54, y: 25 }, tail: { x: 38, y: 2, z: 0 } },
    { leg1: { x: 21, y: 58 }, leg2: { x: 41, y: 64 }, leg3: { x: 53, y: 50 }, leg4: { x: 69, y: 53 }, tail: { x: 72, y: 22, z: 0 } },
    { leg1: { x: 22, y: 59 }, leg2: { x: 30, y: 64 }, leg3: { x: 56, y: 60 }, leg4: { x: 68, y: 62 }, tail: { x: 78, y: 40, z: 0 } },
    { leg1: { x: 47, y: 45 }, leg2: { x: 24, y: 53 }, leg3: { x: 68, y: 68 }, leg4: { x: 47, y: 73 }, tail: { x: 65, y: 65, z: 1 } }
  ];

  const control = { x: null, y: null, angle: null };

  const distance = 30;
  const nearestN = (x, n) => x === 0 ? 0 : (x - 1) + Math.abs(((x - 1) % n) - n);
  const radToDeg = rad => Math.round(rad * (180 / Math.PI));
  const degToRad = deg => deg / (180 / Math.PI);
  const overlap = (a, b) => {
    const buffer = 20;
    return Math.abs(a - b) < buffer;
  };

  const rotateCoord = ({ angle, origin, x, y }) => {
    const a = degToRad(angle);
    const aX = x - origin.x;
    const aY = y - origin.y;
    return {
      x: (aX * Math.cos(a)) - (aY * Math.sin(a)) + origin.x,
      y: (aX * Math.sin(a)) + (aY * Math.cos(a)) + origin.y,
    };
  };

  const setStyles = ({ target, h, w, x, y }) => {
    if (h) target.style.height = h;
    if (w) target.style.width = w;
    target.style.transform = `translate(${x || 0}, ${y || 0})`;
  };

  const targetAngle = dogData => {
    if (!dogData) return;
    const angle = radToDeg(Math.atan2(dogData.pos.y - control.y, dogData.pos.x - control.x)) - 90;
    const adjustedAngle = angle < 0 ? angle + 360 : angle;
    return nearestN(adjustedAngle, 45);
  };

  const reachedTheGoalYeah = (x, y) => {
    return overlap(control.x, x) && overlap(control.y, y);
  };

  // Anstelle von dog.childNodes nutzen wir querySelectorAll auf dem Hund-Element
  function positionLegs(dogEl, frame) {
    const legs = dogEl.querySelectorAll('.leg');
    legs.forEach((leg, i) => {
      const pos = partPositions[frame][`leg${i + 1}`];
      setStyles({ target: leg, x: px(pos.x), y: px(pos.y) });
    });
  }

  function moveLegs(dogEl) {
    const legs = dogEl.querySelectorAll('.leg');
    // Annahme: Reihenfolge entspricht den Beinen 1 bis 4
    if (legs[0]) legs[0].classList.add('walk-1');
    if (legs[1]) legs[1].classList.add('walk-2');
    if (legs[2]) legs[2].classList.add('walk-2');
    if (legs[3]) legs[3].classList.add('walk-1');
  }

  function stopLegs(dogEl) {
    const legs = dogEl.querySelectorAll('.leg');
    if (legs[0]) legs[0].classList.remove('walk-1');
    if (legs[1]) legs[1].classList.remove('walk-2');
    if (legs[2]) legs[2].classList.remove('walk-2');
    if (legs[3]) legs[3].classList.remove('walk-1');
  }

  function positionTail(dogEl, frame) {
    const tail = dogEl.querySelector('.tail');
    const pos = partPositions[frame].tail;
    setStyles({ target: tail, x: px(pos.x), y: px(pos.y) });
    tail.style.zIndex = pos.z;
    tail.classList.add('wag');
  }

  function animateDog({ target, frameW, currentFrame, end, data, part, speed, direction }) {
    const offset = direction === 'clockwise' ? 1 : -1;
    target.style.transform = `translateX(${px(data.animation[currentFrame][0] * -frameW)})`;
    if (part === 'body') {
      positionLegs(data.dog, currentFrame);
      moveLegs(data.dog);
      positionTail(data.dog, currentFrame);
    } else {
      target.parentNode.classList.add('happy');
    }
    data.angle = angles[currentFrame];
    data.index = currentFrame;
    if (data.animation[currentFrame][1] === 'f') {
      target.parentNode.classList.add('flip');
    } else {
      target.parentNode.classList.remove('flip');
    }
    let nextFrame = currentFrame + offset;
    nextFrame = nextFrame === -1 ? data.animation.length - 1 : nextFrame === data.animation.length ? 0 : nextFrame;
    if (currentFrame !== end) {
      data.timer[part] = setTimeout(() => animateDog({ target, data, part, frameW, currentFrame: nextFrame, end, direction, speed }), speed || 150);
    } else if (part === 'body') {
      control.angle = angles[end];
      data.walk = true;
      setTimeout(() => { stopLegs(data.dog); }, 200);
      setTimeout(() => {
        const happyEl = data.dog.querySelector('.happy');
        if (happyEl) happyEl.classList.remove('happy');
      }, 5000);
    }
  }

  function triggerDogAnimation({ target, frameW, start, end, data, speed, part, direction }) {
    clearTimeout(data.timer[part]);
    data.timer[part] = setTimeout(() => animateDog({ target, data, part, frameW, currentFrame: start, end, direction, speed }), speed || 150);
  }

  function getDirection({ pos, facing, target }) {
    const dx2 = facing.x - pos.x;
    const dy1 = pos.y - target.y;
    const dx1 = target.x - pos.x;
    const dy2 = pos.y - facing.y;
    return dx2 * dy1 > dx1 * dy2 ? 'anti-clockwise' : 'clockwise';
  }

  function turnDog({ dog: dogData, start, end, direction }) {
    const head = dogData.dog.querySelector('.head');
    const body = dogData.dog.querySelector('.body');
    triggerDogAnimation({ target: head, frameW: 31 * 2, start, end, data: dogData, speed: 100, direction, part: 'head' });
    setTimeout(() => {
      triggerDogAnimation({ target: body, frameW: 48 * 2, start, end, data: dogData, speed: 100, direction, part: 'body' });
    }, 200);
  }

  function createDog() {
    const dogEl = elements.dog;
    const rect = dogEl.getBoundingClientRect();
    dogEl.style.left = px(rect.left);
    dogEl.style.top = px(rect.top);
    positionLegs(dogEl, 0);
    const index = 0;
    const dogData = {
      timer: { head: null, body: null, all: null },
      pos: { x: rect.left + rect.width / 2, y: rect.top + rect.height / 2 },
      actualPos: { x: rect.left, y: rect.top },
      facing: { x: rect.left + rect.width / 2, y: rect.top + rect.height / 2 + 30 },
      animation: animationFrames.rotate,
      angle: 360,
      index,
      dog: dogEl
    };
    elements.dogData = dogData;
    turnDog({ dog: dogData, start: index, end: defaultEnd, direction: 'clockwise' });
    positionTail(dogEl, 0);
  }

  function checkBoundaryAndUpdateDogPos(x, y, dogEl, dogData) {
    const lowerLimit = -40;
    const upperLimit = 40;
    if (x > lowerLimit && x < (elements.wrapper.clientWidth - upperLimit)) {
      dogData.pos.x = x + 48;
      dogData.actualPos.x = x;
    }
    if (y > lowerLimit && y < (elements.wrapper.clientHeight - upperLimit)) {
      dogData.pos.y = y + 48;
      dogData.actualPos.y = y;
    }
    dogEl.style.left = px(x);
    dogEl.style.top = px(y);
  }

  function positionMarker(i, pos) {
    elements.marker[i].style.left = px(pos.x);
    elements.marker[i].style.top = px(pos.y);
  }

  function moveDog() {
    clearInterval(elements.dogData.timer.all);
    const dogEl = elements.dogData.dog;
    elements.dogData.timer.all = setInterval(() => {
      const rect = dogEl.getBoundingClientRect();
      const start = angles.indexOf(elements.dogData.angle);
      const end = angles.indexOf(targetAngle(elements.dogData));
      if (reachedTheGoalYeah(rect.left + 48, rect.top + 48)) {
        clearInterval(elements.dogData.timer.all);
        const { x, y } = elements.dogData.actualPos;
        dogEl.style.left = px(x);
        dogEl.style.top = px(y);
        stopLegs(dogEl);
        turnDog({ dog: elements.dogData, start, end: defaultEnd, direction: 'clockwise' });
        return;
      }
      let { x, y } = elements.dogData.actualPos;
      const dir = directionConversions[targetAngle(elements.dogData)];
      if (dir !== 'up' && dir !== 'down') x += (dir.includes('left')) ? -distance : distance;
      if (dir !== 'left' && dir !== 'right') y += (dir.includes('up')) ? -distance : distance;
      positionMarker(0, elements.dogData.pos);
      positionMarker(1, control);
      const rotated = rotateCoord({
        angle: elements.dogData.angle,
        origin: elements.dogData.pos,
        x: elements.dogData.pos.x,
        y: elements.dogData.pos.y - 100
      });
      elements.dogData.facing.x = rotated.x;
      elements.dogData.facing.y = rotated.y;
      positionMarker(2, elements.dogData.facing);
      const currentStart = angles.indexOf(elements.dogData.angle);
      const currentEnd = angles.indexOf(targetAngle(elements.dogData));
      if (currentStart === currentEnd) {
        elements.dogData.turning = false;
      }
      if (!elements.dogData.turning && elements.dogData.walk) {
        if (currentStart !== currentEnd) {
          elements.dogData.turning = true;
          const newDirection = getDirection({
            pos: elements.dogData.pos,
            facing: elements.dogData.facing,
            target: control
          });
          turnDog({ dog: elements.dogData, start: currentStart, end: currentEnd, direction: newDirection });
        } else {
          checkBoundaryAndUpdateDogPos(x, y, dogEl, elements.dogData);
          moveLegs(dogEl);
        }
      }
    }, 200);
  }

  createDog();

  function triggerTurnDog() {
    const dogData = elements.dogData;
    dogData.walk = false;
    control.angle = null;
    const newDirection = getDirection({
      pos: dogData.pos,
      facing: dogData.facing,
      target: control
    });
    const start = angles.indexOf(dogData.angle);
    const end = angles.indexOf(targetAngle(dogData));
    turnDog({ dog: dogData, start, end, direction: newDirection });
  }

  elements.wrapper.addEventListener('mousemove', e => {
    control.x = e.pageX;
    control.y = e.pageY;
    triggerTurnDog();
  });

  elements.wrapper.addEventListener('click', () => {
    if (isActionActive) return;
    moveDog();
  });
}

onMounted(() => {
  init();
});

/* Action-Funktionen – während einer Aktion wird die Mausverfolgung deaktiviert */
function makeDogJump() {
  const dogEl = dog.value;
  if (!dogEl) return;
  isActionActive = true;
  dogEl.classList.add('jump');
  setTimeout(() => {
    dogEl.classList.remove('jump');
    isActionActive = false;
  }, 600);
}

function makeDogSit() {
  const dogEl = dog.value;
  if (!dogEl) return;
  isActionActive = true;
  dogEl.classList.add('sit');
  setTimeout(() => {
    dogEl.classList.remove('sit');
    isActionActive = false;
  }, 2000);
}

function makeDogLieDown() {
  const dogEl = dog.value;
  if (!dogEl) return;
  isActionActive = true;
  dogEl.classList.add('liedown');
  setTimeout(() => {
    dogEl.classList.remove('liedown');
    isActionActive = false;
  }, 2000);
}

function makeDogDrink() {
  const dogEl = dog.value;
  if (!dogEl) return;
  isActionActive = true;
  dogEl.classList.add('drink');
  setTimeout(() => {
    dogEl.classList.remove('drink');
    isActionActive = false;
  }, 2000);
}

function makeDogEat() {
  const dogEl = dog.value;
  if (!dogEl) return;
  isActionActive = true;
  dogEl.classList.add('eat');
  setTimeout(() => {
    dogEl.classList.remove('eat');
    isActionActive = false;
  }, 2000);
}

function makeDogSleep() {
  const dogEl = dog.value;
  if (!dogEl) return;
  isActionActive = true;
  dogEl.classList.add('sleep');
  setTimeout(() => {
    dogEl.classList.remove('sleep');
    isActionActive = false;
  }, 3000);
}

function makeDogShower() {
  const dogEl = dog.value;
  if (!dogEl) return;
  isActionActive = true;
  dogEl.classList.add('shower');
  setTimeout(() => {
    dogEl.classList.remove('shower');
    isActionActive = false;
  }, 2000);
}

function makeDogPlay() {
  const dogEl = dog.value;
  if (!dogEl) return;
  isActionActive = true;
  dogEl.classList.add('play');
  setTimeout(() => {
    dogEl.classList.remove('play');
    isActionActive = false;
  }, 2000);
}

</script>

<style scoped>
* {
  box-sizing: border-box;
}
body {
  padding: 0;
  margin: 0;
  font-family: sans-serif;
  background-color: rgb(248, 219, 130);
}
p, h1, h2, h3, h4 {
  display: inline-block;
  margin: 0;
  padding: 0;
}

/* Wrapper für den Hund */
.wrapper {
  position: absolute;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* Basis-Stile für die Gliedmaßen, Body, Head etc. */
.leg {
  position: absolute;
  background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAgAAAAMCAYAAABfnvydAAAAAXNSR0IArs4c6QAAAElJREFUKFNjZICC/////4exQTQjIyMjmAYR6JIwhSBFjLMiLP6nLj+OrBnOnh1pyTBSFIA8jS0sQGGQtuIEJDhhipBDCyQJ4gMALug8VaRjkWwAAAAASUVORK5CYII=);
  width: calc(2 * 8px);
  height: calc(2 * 12px);
  background-size: calc(2 * 8px) calc(2 * 12px) !important;
  transition: 0.15s;
}

.body {
  position: absolute;
  background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAASAAAAAwCAYAAACxIqevAAAAAXNSR0IArs4c6QAABSpJREFUeF7tm32a1CAMxqe30pvoAfRQegC9id5qfPBZlGELJOQL2nf/2t0pkPySvA20czzwAwIgAAJBBI6gdbEsCIAACDwgQEgCEACBMAIQoDD0WBgEQAAChBwAARB4fPv04UnF8PXnbzXdUJuIajyuAwEQWINAKTpffvwiG/X988d/10rFCAJExo4LQeA6BJL4cESn5XkSI4kIQYCuk1PwBARIBLTEJy8mESEIEClkuAgErkFAW3ykIgQBukZewQsQIBGAAJEw4SIQAAFtAlbiI+mC0AFpRxnzgcCiBCBAiwYGZvkRGL1vInmi4ufFnitBgPaMG6xWIJCFZ/ToN79jAiFSgF5N8Xw+yS8bzq5+HAdrV8W6eNYojLsvAarw1IQ0X3a7L/1XzyFAyIRbEdBq+dEVydNGKxYjS7jvBKEDGhHF59MEtO+4EKLpUPz9rtdo+zs/+/+RECANiphDTEBbfEqDIoQoFfDZudTZ/3vXZj/quVpjxIF4m+DyAjR6ulGDXPWQkerHqvZrJaxknlWTXepTGl/GPedK/b/0dy0oPYHJn1mK0KoxEW3BZr9NmwK5wiGjxP6czBF3Y0kheYz1SvacRx43g1okzkRjJDo9wcpxsRIhr5i4bcE0HeIaLS2i2SczvXU9faB2aT17LYvWcvt15pMH+1IYer9TOv16PGWMJOc1a5ViByceUx2QhUMcoykQWtdY2F52QxaFbSGYNR/u+xsRfCNvAL1znRzzUQd01uVQx6ya89IbAluAdizgMvDWTwI0hdRDeLSFyDI/RkWoyb5eSypAZdd6dmZkuQXzjgknDrcRIK8gcOD3CsrL3jMbJN1QpN3JF4ntI4Hb9XPvmHBq4BYC5BkADvzVtjHZHkkRe7Ju8ZPYv6vIrHQz49QAS4A8kotjPCVZPGwu7ZDa722vdhGvYL80BmdMemc1nEPl1lYsrdna5lHyHAIkpfQ2XjN5IopBan+EzZrbsBXsl8ZAIkC9s5zRgTMEaCAiHsmllTwetkqfALQS3fqgnHKvmN3GRHHX7EIhQJQMaV/DqWHyFswzsTgOtDB4v4uS7NCw25Nzi53Ujwj2HgJU82o9fm9tp9ABvc84CJBM7F9GSws3J250ByT1I1pEpfZHdUCKqfhuKq+YcNlfUoC8YFvcdSNsr7OVm0QrbiVnt5AtEaAeQvfOgPINJl/j+YVUr7zi5g4ESOm2wwXfS/TdO6CyCCN80YqFUmosMQ0EiBEGaQJ5wc4uSe0t0XjbfhaWnf3RtJ2Rsltcap1bM+zRASmkzgz43rLWiTJy2cKftKZHN6Rt+4jVbp/n95A0Y5GYp5+Z70FCgIQZZJHwkQJk4Y/HlkxSBMIU2HK4hhBpMIcACdNH+7CzLFavrsFiK9k749L0S6MIhCmw9fAZIdJkDgESpI9lt+ApRJoJRcVZfiVhZjsQYTPVtx2vK+Mxsn9mq9WaEwI0ot343EN8yqWlBVu7kQt4du8+ia07jFoEmgVg4QfmpBMgC1Ca0uNsQquwLd/G1bKRHqbXK6mF2pofBTxLHuO0CVxWgKzEMlp8tBMA84FAJIHLCpBFxwbxiUxVrH1FAiwBsijqEqpFgc+c8rfOS7B1uWIJwKdIApcXoAx35hAXT1oiUxNr34EAW4CsuiCL7qcVQOohLjqeO5QAfIwkMCVA2iLkKT6RsLE2CIDAK4FpAcoilKfjvky24nsoSA4QAAFfAiIBKk2lbmvyGGxvfAON1UBgRQJqArSic7AJBEBgbQJ/AOoMmm0ZeBaqAAAAAElFTkSuQmCC);
  width: calc(2 * 6 * 48px);
  height: calc(2 * 48px);
  background-size: calc(2 * 6 * 48px) calc(2 * 48px) !important;
}

.dog {
  position: absolute;
  width: calc(2 * 48px);
  height: calc(2 * 48px);
  animation: fade-in forwards 1s;
  transition: 0.5s;
}

@keyframes fade-in {
  0% { opacity: 0; }
  100% { opacity: 1; }
}

.head {
  position: absolute;
  background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAALoAAAAgCAYAAACl82LUAAAAAXNSR0IArs4c6QAAA+hJREFUeF7tmtuV1DAMhiddQSdQABS1FACdQFezx4cVR2h1+X1LnJXnaSbjRLfPkhz7eOzP9kACDxwJbNwmbg88bgP6y5dPz++//txG383WWh64BTgF8m8/fz9+fP382LCvBdBdtNmgN0Rqteqymj4NLp1+CwR6rSNrx3tWUjanMVdn9dWqy2r69BJbyw46PgRdOrL8towpbcVox3N5pX0pnwK7/F6u0Wdke0PyqXWydJghuzzT8rdm/0i7e4FtuX8ma1Wgk3M9IwjCEZlXZnMpl8viuhH0FHiCpQYEDjivJlKO9ImU3Rrwcl/kbz7hKQHU2Nii28x7OOiR7TzhIay5oGttg6fAKMdrkGkOtkC3xiIQeLI1+yx/IM6XeqJ2WxOPrh/HESawmcC2PHs2a6pDrJklA21lWBmImqBHWbwmu2r6ebCjstFAjrA78rmlSyTb6m21695Yq2VDe+ezWHsH+uhgczCjjFojuya7IjrUyEZBR9sJS3ZNxaqtYlo7Z12jtRePnwcy/RfBfqbP/wN9luCzQdMWqx50V9pd9JoFemQzB1iDU4Kq/S4yvAngVQOkD69JKnysrGgfHnQKtgwId8qVoEeQR/14BILVwnAAve/y+VpVlvej91wC+uxge1k9q2wkm7eCLquaBBTp0aOMTrpZE4Xs02TPhFxj7V9Gzwrbqna3LkK1iaFl9V7QqZ9vaV2u8PkG/e0cTdQCRP/T+3MvU8nXfl7Ae0H3+tXIltn/b9Ane9jKbCPKqAe6tWt7RcAnuxh6vLcuKQ+oiUfkd2qbUmV0bSPlTNi0Ml8TVIQiHnhvl9rrv2sWl1YLY/Xn5frz+VSPkSBVUfoguodingZ0bwNlFuzRTnGrXCu4vHLwszDRmxIJpda/I9fQxasFujWRNbuQSV/GvAPdm2noQ5Fx1vZ0a9ARmWXMFaCTXNJRA6426PyZ3vGDDfpfry+V0SmjjC7jfBJE5z9mTzTudK7XLLmUBa3daN5yyImIZG+tNUEzemSzthBHr3Hf8uS2zIbRTNijcx/knCgAaPXQxnkTrVeutRNsTa4I0lGge/6qeeNkHYXw3ky5O6MfDbYoq2mBoEw3qrqgOvTKlX17NLnRxShPAlavb7Vm1rt6JLFYfTnSr2u2m6cXizIjgo0GWpbzHvktMiX0PeBZrxORitAjl/fv0QE6RJczxvTaK1uV8luzPTyP3mtsj8PJCXT4CNWlR6aX5RH5I2VrfbSlw0i5iJ2jx1ivKb21BHUgVkXhOt7ugP5oB+/n5fDABj1HnNNbuUFPj0AOB2zQc8Q5vZUb9PQI5HDABj1HnNNbuUFPj0AOB7wCKr30XX1N2MUAAAAASUVORK5CYII=);
  width: calc(2 * 6 * 31px);
  height: calc(2 * 32px);
  background-size: calc(2 * 6 * 31px) calc(2 * 32px) !important;
}

.head-wrapper.happy {
  animation: infinite 0.5s pant;
}

@keyframes pant {
  0%, 100% { transform: translateY(-1px); }
  50% { transform: translateY(1px); }
}

.head-wrapper.flip.happy {
  animation: infinite 0.5s pant-flip;
}

@keyframes pant-flip {
  0%, 100% { transform: translateY(-1px) scale(-1, 1); }
  50% { transform: translateY(1px) scale(-1, 1); }
}

.tail {
  position: absolute;
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAgAAAAICAYAAADED76LAAAAAXNSR0IArs4c6QAAADZJREFUKFNjZICCWREW/2FsEJ224gQjiAYTIMnU5ceR5RlmR1qCFTFik4SpBCmihwKCjiTkTQB1sCqti9mJ/QAAAABJRU5ErkJggg==);
  width: calc(2 * 8px);
  height: calc(2 * 8px);
  background-size: calc(2 * 8px) !important;
}

.tail-wrapper {
  position: absolute;
  width: calc(2 * 8px);
  height: calc(2 * 8px);
  transition: 0.15s;
}

.body-wrapper {
  position: absolute;
  width: calc(2 * 48px);
  height: calc(2 * 48px);
  overflow: hidden;
}

.body-wrapper,
.head-wrapper {
  z-index: 1;
}

.walk-1 {
  animation: infinite 0.4s walking;
  animation-delay: 0;
}

.walk-2 {
  animation: infinite 0.4s walking;
  animation-delay: 0.2s;
}

@keyframes walking {
  0%, 100% { transform: translateY(-4px); }
  50% { transform: translateY(0); }
}

.wag {
  animation: infinite 0.5s wag;
}

@keyframes wag {
  0%, 100% { transform: translateX(-2px); }
  50% { transform: translateX(2px); }
}

.head-wrapper {
  position: absolute;
  top: 6px;
  left: 16px;
  width: calc(2 * 31px);
  height: calc(2 * 32px);
  overflow: hidden;
}

.flip {
  transform: scale(-1, 1);
}

.img-bg {
  image-rendering: pixelated;
  background-repeat: no-repeat !important;
}

.sign {
  position: absolute;
  color: #9a5838;
  bottom: 10px;
  right: 10px;
  font-size: 10px;
}

a {
  color: #9a5838;
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}

.indicator {
  position: fixed;
  top: 10px;
  left: 10px;
  color: #9a5838;
}

.d-none {
  display: none;
}

.indicator {
  position: fixed;
  top: 10px;
  right: 10px;
}

.marker {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  position: absolute;
  transition: 0.5s;
  z-index: 100;
  margin-top: -5px;
  margin-left: -5px;
}

.red {
  background-color: rgb(255, 64, 0);
}

.green {
  background-color: rgb(42, 239, 190);
}

.blue {
  background-color: rgb(0, 140, 255);
}

/* Animationen für Aktionen */
.jump {
  animation: jump 0.5s ease-in-out;
}
@keyframes jump {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-50px); }
}
.sit {
  animation: sitPose 0.3s forwards;
  position: absolute;
  bottom: 10px;
}
@keyframes sitPose {
  0% { transform: translateY(0) scaleY(1); }
  100% { transform: translateY(15px) scaleY(1) rotateX(0deg); }
}
.liedown {
  animation: lieDown 0.5s forwards;
}
@keyframes lieDown {
  0% { transform: translateY(0); }
  100% { transform: translateY(20px) rotateX(15deg); }
}
.drink {
  animation: drinkAnim 0.5s forwards;
}
@keyframes drinkAnim {
  0% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
  100% { transform: translateY(0); }
}
.eat {
  animation: eatAnim 0.5s forwards;
}
@keyframes eatAnim {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}
.sleep {
  animation: sleepAnim 0.5s forwards;
}
@keyframes sleepAnim {
  0% { transform: translateY(0); }
  100% { transform: translateY(10px); opacity: 0.8; }
}
.shower {
  animation: showerAnim 0.5s forwards;
}
@keyframes showerAnim {
  0% { transform: rotate(0deg); }
  50% { transform: rotate(10deg); }
  100% { transform: rotate(0deg); }
}
.play {
  animation: playAnim 0.4s infinite alternate;
}
@keyframes playAnim {
  0%, 100% { transform: rotate(0deg); }
  50% { transform: rotate(10deg); }
}

/* Controls */
.controls {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  z-index: 1000;
}
.controls button {
  pointer-events: auto;
}
</style>
