<template>
  <div>
    <title-bar :title-stack="titleStack" />
    <hero-bar>
      회원가입
      <router-link slot="right" to="/profile" class="button">
        마이 페이지
      </router-link>
    </hero-bar>
    <section>
      <SignUpForm></SignUpForm>
    </section>
  </div>
</template>

<script>
import SignUpForm from "@/components/SignUpForm.vue";
import TitleBar from "@/components/TitleBar";
import HeroBar from "@/components/HeroBar";

export default {
  components: {
    SignUpForm,
    TitleBar,
    HeroBar
  },
  computed: {
    titleStack() {
      return ["Admin", "SignUpForm"];
    }
  }
};
</script>
