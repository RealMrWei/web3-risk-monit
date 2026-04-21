const hre = require("hardhat");

async function main() { 
    // get contract
    const Vault = await hre.ethers.getContractFactory("Vault");

    // deploy contract
    const vault = await Vault.deploy();
    await vault.waitForDeployment();

    // get contract address
    console.log("Vault deployed to:", vault.target);
}

main()
  .catch((error) => {
    console.error(error);
    process.exitCode = 1;
  });