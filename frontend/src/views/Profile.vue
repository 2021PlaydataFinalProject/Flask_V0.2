<template>
  <div>
    <title-bar :title-stack="titleStack" />
    <hero-bar>
      프로필
      <!-- <router-link slot="right" to="/" class="button">
        Dashboard
      </router-link> -->
    </hero-bar>
    <section class="section is-main-section">
      <tiles>
        <profile-update-form class="tile is-child" />
        <card-component title="프로필" icon="account" class="tile is-child">
          <user-avatar class="image has-max-width is-aligned-center" />
          <hr />
          <b-field label="이름">
            <b-input :value="userName" custom-class="is-static" readonly />
          </b-field>
          <hr />
          <b-field label="이메일">
            <b-input :value="userEmail" custom-class="is-static" readonly />
          </b-field>
        </card-component>
      </tiles>
      <password-update-form />
    </section>
  </div>
</template>

<script>
import { mapState } from "vuex";
import CardComponent from "@/components/CardComponent";
import TitleBar from "@/components/TitleBar";
import HeroBar from "@/components/HeroBar";
import ProfileUpdateForm from "@/components/ProfileUpdateForm";
import PasswordUpdateForm from "@/components/PasswordUpdateForm";
import Tiles from "@/components/Tiles";
import UserAvatar from "@/components/UserAvatar";

export default {
  name: "Profile",
  components: {
    UserAvatar,
    Tiles,
    PasswordUpdateForm,
    ProfileUpdateForm,
    HeroBar,
    TitleBar,
    CardComponent
  },
  computed: {
    titleStack() {
      return ["Admin", "Profile"];
    },
    ...mapState(["userName", "userEmail"])
  }
};
</script>
