<script>
  import { Route, Router } from "svelte-routing";
  import Toasts from "./components/Toasts.svelte";
  import Login from "./components/pages/Login.svelte";
  import Registration from "./components/pages/Registration.svelte";
  import Room from "./components/pages/Room.svelte";
  import Verification from "./components/pages/Verification.svelte";
  import GoogleOAuthRedirect from "./components/pages/google/OAuthRedirect.svelte";
  import ResetPassword from "./components/pages/reset-password/ResetPassword.svelte";
  import SendResetPassword from "./components/pages/reset-password/SendResetPassword.svelte";
  import TelegramOAuthRedirect from "./components/pages/telegram/OAuthRedirect.svelte";
</script>

<Router>
  <Route path="/registration" component={Registration} />
  <Route path="/verification" component={Verification} />
  <Route path="/google/oauth/redirect" component={GoogleOAuthRedirect} />
  <Route path="/tg/oauth/redirect" component={TelegramOAuthRedirect} />
  <Route path="/login" component={Login} />
  <Route path="/reset-password" component={SendResetPassword} />
  <Route path="/reset-password/:sessionId" component={ResetPassword} />
  <Route path="/room/:room" component={Room} />
  <Route component={Room} />
</Router>
<Toasts />
