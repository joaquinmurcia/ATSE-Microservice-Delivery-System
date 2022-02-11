# ase_project

Main repository for the ASE Project

# How to run the project
## Locally, with docker-compose up
In your machine, run...
```
docker swarm init
export GITLAB_REGISTRY_PATH=gitlab.lrz.de:5005/ase-21-22/team-32/ase_project/
```


Create a deploy token with at least read_registry, API and write_registry access in the GitLab copy
Then run...
`docker login -u <your-gitlab-username> -p <deploy-token> gitlab.lrz.de:5005`

This part is a shortcut to get all the images pulled:
`docker stack deploy --compose-file docker-compose.yml stack1`

When everything is pulled, Ctrl+C and do...
`docker stack rm stack1`

Finally...
`docker-compose up`

# Demo of the Raspberry Pi  in action with example workflow:
https://drive.google.com/drive/folders/104-tZQlUf5-F8F6lYqmx27HnmE2UUMkZ?usp=sharing
