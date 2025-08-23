import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../../services/auth';

/** Simple auth guard to protect customer & admin pages that require login. */
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.user()) return true;

  router.navigate(['/auth/login'], { queryParams: { returnUrl: location.pathname } });
  return false;
};
