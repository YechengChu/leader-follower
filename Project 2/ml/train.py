import time

import torch


def get_lr(optimizer):
    return round(optimizer.param_groups[0]['lr'], 8)


def find_optimize_price(config, price_optimizer):
    lr = config['find_optimal_price_lr']
    device = config['device']
    print_interval = config['find_optimal_price_print_interval']
    no_change_epochs = config['find_optimal_price_no_change_epochs']
    round_digits = config['find_optimal_price_round_digits']
    scheduler_patience = config['find_optimal_price_scheduler_patience']

    optimizer = torch.optim.SGD(price_optimizer.parameters(), lr=lr)
    scheduler = torch.optim.lr_scheduler.ReduceLROnPlateau(optimizer, patience=scheduler_patience)

    losses = []
    i = 1
    last_output_time = time.time()
    while len(losses) < no_change_epochs or len(set(losses[-no_change_epochs:])) > 1:
        price_optimizer.train().to(device)
        loss = price_optimizer()
        optimizer.zero_grad()
        loss.backward()
        optimizer.step()

        loss = round(loss.item(), round_digits)
        scheduler.step(loss)
        losses.append(loss)
        curr_time = time.time()
        if print_interval and (curr_time - last_output_time >= print_interval):
            print(f'[optimize] [epochs={i}] [lr={get_lr(optimizer)}] loss: {loss}')
            last_output_time = curr_time
        i += 1

    return price_optimizer.leader_price
