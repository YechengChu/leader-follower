import sys

import torch

from ml.config import follower_mk3_config
from ml.model import PriceOptimizerModel, FollowerReactionModel
from ml.train import find_optimize_price


def main():
    config = follower_mk3_config
    config.update({
        'find_optimal_price_print_interval': None
    })
    follower_reaction_model = FollowerReactionModel(config)

    state_dict_dir = f'ml/{config["output_dir"]}/model_state_dict.torch'
    follower_reaction_model.load_state_dict(torch.load(state_dict_dir))
    price_optimizer = PriceOptimizerModel(follower_reaction_model,
                                          init_price=config['find_optimal_price_init_price'],
                                          device=config['device'],
                                          date=None)
    price = find_optimize_price(config, price_optimizer)
    print(price.item())


if __name__ == '__main__':
    main()
