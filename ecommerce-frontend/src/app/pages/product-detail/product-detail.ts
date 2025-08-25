import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../../services/product';
import { CartService } from '../../services/cart';
import { InteractionService } from '../../services/interaction';
import { InteractionType } from '../../models/interaction-type';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  templateUrl: './product-detail.html',
  styleUrls: ['./product-detail.scss']
})
export class ProductDetailComponent implements OnInit {
  product: any;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
    private interactionService: InteractionService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      const numericId = Number(id);
      this.productService.getById(numericId).subscribe(p => {
        this.product = p;
        this.interactionService.log(InteractionType.VIEW, p.id);
      });
    }
  }

  addToCart() {
    this.cartService.add(this.product);
    this.interactionService.log(InteractionType.ADD_TO_CART, this.product.id);
  }
}
