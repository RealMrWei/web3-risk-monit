// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.20;

// Uncomment this line to use console.log
// import "hardhat/console.sol";

contract Vault {
    event Deposit(address indexed from, uint256 amount);
    event Withdraw(address indexed from, uint256 amount);

    //everyuser has a balance
    mapping(address => uint) public balances;

    // deposit
    function deposit() public payable {
        require(msg.value > 0, "deposit money must be greater than 0");
        balances[msg.sender] += msg.value;
        emit Deposit(msg.sender, msg.value);
    }

    // withdraw
    function withdraw(uint256 amount) public payable {
        require(amount > 0, "withdraw money must be greater than 0");
        require(balances[msg.sender] > 0, "you have no money");
        balances[msg.sender] -= amount;
        (bool success, ) = payable(msg.sender).call{value: amount, gas: 2300}(
            ""
        );
        require(success, "Transfer failed");
        emit Withdraw(msg.sender, amount);
    }

    // get balance
    function getBalance() public view returns (uint256) {
        return balances[msg.sender];
    }
}
