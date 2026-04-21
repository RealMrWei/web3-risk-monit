const hre = require("hardhat");

async function main() { 
    // contract address 
    const contractAddress = "0x5FbDB2315678afecb367f032d93F642f64180aa3";
    
    // connect to contract
    console.log("Connecting to contract...");
    const Vault = await hre.ethers.getContractAt("Vault", contractAddress);
    
    // Test 1: Deposit 1 ETH (normal transaction)
    console.log("\n=== Test 1: Depositing 1 ETH ===");
    const txDeposit1 = await Vault.deposit({ value: hre.ethers.parseEther("1") });
    await txDeposit1.wait();
    console.log("✅ Successfully deposited 1 ETH");
    console.log("Transaction hash:", txDeposit1.hash);
    
    // Test 2: Deposit 3 ETH (large transaction for risk detection)
    console.log("\n=== Test 2: Depositing 3 ETH ===");
    const txDeposit3 = await Vault.deposit({ value: hre.ethers.parseEther("3") });
    await txDeposit3.wait();
    console.log("✅ Successfully deposited 3 ETH");
    console.log("Transaction hash:", txDeposit3.hash);
    
    console.log("\n🎉 All transactions completed!");
    console.log("Please check the Go listener and Java risk service logs for results.");

}

main().catch(console.error);