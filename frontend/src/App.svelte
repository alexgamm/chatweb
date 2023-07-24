<script>
  import { Route, Router } from "svelte-routing";
  import Login from "./components/pages/Login.svelte";
  import Index from "./components/pages/Index.svelte";
  import Registration from "./components/pages/Registration.svelte";
</script>

<Router>
  <Route path="/registration" component={Registration} />
  <Route path="/login" component={Login} />
  <Route path="/" component={Index} />
</Router>
