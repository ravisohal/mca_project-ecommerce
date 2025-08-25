import { InteractionType } from "./interaction-type";

export interface InteractionEvent {
  userId: number;
  productId: number;
  interactionType: InteractionType;
  timestamp: Date;
};
