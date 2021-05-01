base_config = {
    # dataset
    'excel_file': '../data14.xls',
    'sheet_name': 'Follower_Mk1',
    'use_date': False,
    'date': None,

    # training
    'device': 'cpu',
    'output_dir': 'output',
    'train_follower_reaction_no_change_epochs': 30,
    'train_follower_reaction_scheduler_patience': 4,
    'train_follower_reaction_lr': 0.01,
    'train_follower_reaction_round_digits': 5,
    'train_follower_reaction_print_interval':  2,
    'layer_config': [2, 3, 1],

    # price optimizer
    'find_optimal_price_no_change_epochs': 1000,
    'find_optimal_price_scheduler_patience': 50,
    'find_optimal_price_lr': 0.001,
    'find_optimal_price_print_interval': 2,
    'find_optimal_price_round_digits': 4,
    'find_optimal_price_init_price': 1.0,
}


follower_mk1_config = base_config.copy()
follower_mk1_config.update({
    'output_dir': 'follower_mk1_output',
    'sheet_name': 'Follower_Mk1',
    'use_date': False,
    'date': None,
    'layer_config': [2, 'sig', 2, 1],
    'find_optimal_price_init_price': 1.0,
})


follower_mk2_config = base_config.copy()
follower_mk2_config.update({
    'output_dir': 'follower_mk2_output',
    'sheet_name': 'Follower_Mk2',
    'use_date': True,
    'date': 101,
    'layer_config': [10, 'relu', 10, 'relu', 10, 'relu', 10, 'relu', 5, 2, 1],
    'find_optimal_price_init_price': 1.7,
    'train_follower_reaction_no_change_epochs': 30,
    'train_follower_reaction_scheduler_patience': 5,
})


follower_mk3_config = base_config.copy()
follower_mk3_config.update({
    'output_dir': 'follower_mk3_output',
    'sheet_name': 'Follower_Mk3',
    'use_date': False,
    'date': None,
    'layer_config': [4, 'relu', 4, 'relu', 3, 'relu', 2, 1],
    'find_optimal_price_init_price': 1.7,
    'train_follower_reaction_no_change_epochs': 50,
    'train_follower_reaction_scheduler_patience': 10,
})
