<script>
  import { Route, Router } from "svelte-routing";
  import Login from "./components/pages/Login.svelte";
  import Index from "./components/pages/Index.svelte";
  import Registration from "./components/pages/Registration.svelte";
  import Verification from "./components/pages/Verification.svelte";
  import GoogleOAuthRedirect from "./components/pages/google/OAuthRedirect.svelte";
  import TelegramOAuthRedirect from "./components/pages/telegram/OAuthRedirect.svelte";
</script>

<Router>
  <Route path="/registration" component={Registration} />
  <Route path="/verification" component={Verification} />
  <Route path="/google/oauth/redirect" component={GoogleOAuthRedirect} />
  <Route path="/tg/oauth/redirect" component={TelegramOAuthRedirect} />
  <Route path="/login" component={Login} />
  <Route path="/" component={Index} />
</Router>
