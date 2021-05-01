import torch
from torch import nn


class FollowerReactionModel(nn.Module):

    # def init_weights(self):
    #     for layer in self.layers:
    #         if 'Linear' in str(layer):
    #             torch.nn.init.xavier_uniform_(layer.weight)

    def __init__(self, config):
        super().__init__()
        self.config = config
        self.layers = nn.ModuleList()
        last_size = 2 if config['use_date'] else 1
        layer_config = config['layer_config']
        for i in range(0, len(layer_config)):
            if layer_config[i] == 'relu':
                self.layers.append(nn.ReLU())
            elif layer_config[i] == 'sig':
                self.layers.append(nn.Sigmoid())
            else:
                self.layers.append(nn.Linear(last_size, layer_config[i]))
                last_size = layer_config[i]
        if config.get('emb', False):
            self.emb = nn.Linear(1, 2)
            self.emb_linear = nn.Linear(2, 1)

    def forward(self, x):
        if self.config.get('emb', False):
            emb_out = self.emb_linear(self.emb(x[1].unsqueeze(0)))
            x = torch.cat((x[0].unsqueeze(0), emb_out), 0)
        for layer in self.layers:
            x = layer(x)
        return x


def freeze(model):
    for param in model.parameters():
        param.requires_grad = False


class PriceOptimizerModel(nn.Module):
    def __init__(self, follower_reaction_model, device, init_price=1.5, date=None):
        super().__init__()
        self.date = date
        self.device = device
        self.leader_price = nn.Parameter(torch.tensor([init_price]), requires_grad=True)
        self.follower_reaction_model = follower_reaction_model
        freeze(self.follower_reaction_model)

    def forward(self):
        if self.date is not None:
            inp = torch.tensor([self.leader_price, self.date], device=self.device)
        else:
            inp = self.leader_price
        follower_price = self.follower_reaction_model(inp)
        return -((self.leader_price - 1) * (2 - self.leader_price + 0.3 * follower_price))